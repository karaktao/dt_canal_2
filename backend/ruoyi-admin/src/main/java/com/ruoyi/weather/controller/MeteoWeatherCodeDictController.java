package com.ruoyi.weather.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.weather.domain.MeteoWeatherCodeDict;
import com.ruoyi.weather.service.IMeteoWeatherCodeDictService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * weatherCodeDictController
 * 
 * @author dt
 * @date 2025-07-03
 */
@RestController
@RequestMapping("/weather/codeDict")
public class MeteoWeatherCodeDictController extends BaseController
{
    @Autowired
    private IMeteoWeatherCodeDictService meteoWeatherCodeDictService;

    /**
     * 查询weatherCodeDict列表
     */
    @PreAuthorize("@ss.hasPermi('weather:codeDict:list')")
    @GetMapping("/list")
    public TableDataInfo list(MeteoWeatherCodeDict meteoWeatherCodeDict)
    {
        startPage();
        List<MeteoWeatherCodeDict> list = meteoWeatherCodeDictService.selectMeteoWeatherCodeDictList(meteoWeatherCodeDict);
        return getDataTable(list);
    }

    /**
     * 导出weatherCodeDict列表
     */
    @PreAuthorize("@ss.hasPermi('weather:codeDict:export')")
    @Log(title = "weatherCodeDict", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MeteoWeatherCodeDict meteoWeatherCodeDict)
    {
        List<MeteoWeatherCodeDict> list = meteoWeatherCodeDictService.selectMeteoWeatherCodeDictList(meteoWeatherCodeDict);
        ExcelUtil<MeteoWeatherCodeDict> util = new ExcelUtil<MeteoWeatherCodeDict>(MeteoWeatherCodeDict.class);
        util.exportExcel(response, list, "weatherCodeDict数据");
    }

    /**
     * 获取weatherCodeDict详细信息
     */
    @PreAuthorize("@ss.hasPermi('weather:codeDict:query')")
    @GetMapping(value = "/{code}")
    public AjaxResult getInfo(@PathVariable("code") Long code)
    {
        return success(meteoWeatherCodeDictService.selectMeteoWeatherCodeDictByCode(code));
    }

    /**
     * 新增weatherCodeDict
     */
    @PreAuthorize("@ss.hasPermi('weather:codeDict:add')")
    @Log(title = "weatherCodeDict", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MeteoWeatherCodeDict meteoWeatherCodeDict)
    {
        return toAjax(meteoWeatherCodeDictService.insertMeteoWeatherCodeDict(meteoWeatherCodeDict));
    }

    /**
     * 修改weatherCodeDict
     */
    @PreAuthorize("@ss.hasPermi('weather:codeDict:edit')")
    @Log(title = "weatherCodeDict", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MeteoWeatherCodeDict meteoWeatherCodeDict)
    {
        return toAjax(meteoWeatherCodeDictService.updateMeteoWeatherCodeDict(meteoWeatherCodeDict));
    }

    /**
     * 删除weatherCodeDict
     */
    @PreAuthorize("@ss.hasPermi('weather:codeDict:remove')")
    @Log(title = "weatherCodeDict", businessType = BusinessType.DELETE)
	@DeleteMapping("/{codes}")
    public AjaxResult remove(@PathVariable Long[] codes)
    {
        return toAjax(meteoWeatherCodeDictService.deleteMeteoWeatherCodeDictByCodes(codes));
    }
}
