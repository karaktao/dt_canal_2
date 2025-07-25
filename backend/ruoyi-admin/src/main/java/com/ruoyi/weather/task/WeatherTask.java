package com.ruoyi.weather.task;


import cn.hutool.json.JSONUtil;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.weather.domain.MeteoDailyResponse;
import com.ruoyi.weather.domain.MeteoWeatherDaily;
import com.ruoyi.weather.service.IMeteoWeatherDailyService;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component("weatherTask")
@DisallowConcurrentExecution
public class WeatherTask {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IMeteoWeatherDailyService dailyService;

    private static final String API_URL =
            "https://api.open-meteo.com/v1/forecast?latitude=55.55&longitude=6.23&daily=temperature_2m_max,temperature_2m_min,wind_direction_10m_dominant,wind_speed_10m_max,precipitation_sum,precipitation_probability_max&models=best_match&timezone=Europe%2FAmsterdam";


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void fetchDailyForecast() {
        try {
            System.out.println("执行无参方法");


            // 1. 发送 GET 请求并打印原始 JSON
            String s = HttpUtils.sendGet(API_URL);
            System.out.println("打印" + s);

            // 2. 反序列化成我们自定义的 DTO
            MeteoDailyResponse resp = JSONUtil.toBean(s, MeteoDailyResponse.class);
            System.out.println("解析后 DTO: " + resp);

            // 3. 提取元数据
            String latitude            = resp.getLatitude();
            String longitude           = resp.getLongitude();
            String timezone            = resp.getTimezone();
            Integer utcOffsetSeconds   = resp.getUtcOffsetSeconds();
            String elevation           = resp.getElevation();

            // 4. 准备一个列表（可选）来收集处理后的实体
            List<MeteoWeatherDaily> meteoWeatherDailies = new ArrayList<>();

         //5. 遍历每天的数据
            for (int i = 0; i < resp.getDaily().getTime().size(); i++) {


                String date  = new SimpleDateFormat("yyyy-MM-dd")
                        .format(resp.getDaily().getTime().get(i));

                System.out.println("本次要处理的日期数量1: " + resp.getDaily().getTime().size());


                // 2. 直接做 java.sql.Date（只有年-月-日，没有时分秒）
                java.sql.Date sqlDate = java.sql.Date.valueOf(date);

                // 3. 用 sqlDate 去查、去存
                MeteoWeatherDaily daily = dailyService
                        .selectByLocationAndDate(latitude, longitude, sqlDate);

                boolean isNew = (daily == null);
                if (isNew) {
                    daily = new MeteoWeatherDaily();
                }

                // 5.4 设置元数据字段
                daily.setLatitude(latitude);
                daily.setLongitude(longitude);
                daily.setTimezone(timezone);
                daily.setUtcOffsetSeconds(utcOffsetSeconds);
                daily.setElevation(elevation);
                daily.setWeatherDate(sqlDate);

                daily.setTemperatureMax(
                        resp.getDaily().getTemperature2mMax().get(i).toString()
                );
                daily.setTemperatureMin(
                        resp.getDaily().getTemperature2mMin().get(i).toString()
                );
                daily.setWindDirectionDominant(
                        resp.getDaily().getWindDirection10mDominant().get(i)
                );
                daily.setWindSpeedMax(
                        resp.getDaily().getWindSpeed10mMax().get(i).toString()
                );
                daily.setPrecipitationSum(
                        resp.getDaily().getPrecipitationSum().get(i).toString()
                );
                daily.setPrecipitationProbabilityMax(
                        resp.getDaily().getPrecipitationProbabilityMax().get(i)
                );

                // 5.6 设置创建/更新时间
                daily.setCreatedAt(new Date());

                // 5.7 插入或更新
                if (isNew) {
                    dailyService.insertMeteoWeatherDaily(daily);
                } else {
                    dailyService.updateMeteoWeatherDaily(daily);
                }

                // 5.8 （可选）将处理后的对象加入列表
                meteoWeatherDailies.add(daily);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

