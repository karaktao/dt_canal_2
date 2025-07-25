package com.ruoyi.weather.task;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.infra.domain.RwsCatalogus;
import com.ruoyi.infra.mapper.RwsCatalogusMapper;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("waterLevelCheckTask")
@DisallowConcurrentExecution
public class WaterLevelCheckTask {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RwsCatalogusMapper catalogusMapper;

    private static final String API_URL = "/api/rijks/ONLINEWAARNEMINGENSERVICES_DBO/OphalenWaarnemingen";

    @Transactional
    public void checkWaterLevelAvailability() {

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("meta_compartiment_code", "OW");
        queryWrapper.eq("meta_grootheid_code", "WATHTE");
        List<RwsCatalogus> locations = catalogusMapper.selectList(queryWrapper);

        for (RwsCatalogus loc : locations) {
            try {
                Map<String, Object> payload = new HashMap<>();

                Map<String, Object> locatie = new HashMap<>();
                locatie.put("Code", loc.getLocCode());
                locatie.put("X", loc.getLocX());
                locatie.put("Y", loc.getLocY());

                Map<String, Object> aquoMetadata = new HashMap<>();
                Map<String, Object> compartiment = new HashMap<>();
                compartiment.put("Code", "OW");
                Map<String, Object> grootheid = new HashMap<>();
                grootheid.put("Code", "WATHTE");

                aquoMetadata.put("Compartiment", compartiment);
                aquoMetadata.put("Grootheid", grootheid);

                Map<String, Object> waarnemingMetadata = new HashMap<>();
                waarnemingMetadata.put("AquoMetadata", aquoMetadata);

                Map<String, Object> periode = new HashMap<>();
                periode.put("Begindatumtijd", "2025-07-08T01:20:00.000+01:00");
                periode.put("Einddatumtijd", "2025-07-08T01:40:00.000+01:00");

                payload.put("Locatie", locatie);
                payload.put("AquoPlusWaarnemingMetadata", waarnemingMetadata);
                payload.put("Periode", periode);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<String> entity = new HttpEntity<>(JSONUtil.toJsonStr(payload), headers);
                ResponseEntity<String> response = restTemplate.exchange(
                        API_URL, HttpMethod.POST, entity, String.class
                );

                String body = response.getBody();
                boolean hasWaterLevel = true;
                if (body != null && body.contains("\"Succesvol\": false") && body.contains("Geen gegevens gevonden")) {
                    hasWaterLevel = false;
                }

                // 更新数据库中的 hasWaterLevel 字段
                loc.setHasWaterLevel(hasWaterLevel ? "true" : "false");
                catalogusMapper.updateRwsCatalogus(loc);

                System.out.println("[" + loc.getLocCode() + "] has water level: " + hasWaterLevel);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
