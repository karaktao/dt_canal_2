package com.ruoyi.weather.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.weather.domain.MeteoWeatherDaily;
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
import com.ruoyi.weather.service.IMeteoWeatherDailyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * weatherDailyController
 * 
 * @author DT
 * @date 2025-07-03
 */
@RestController
@RequestMapping("/weather/daily")
public class MeteoWeatherDailyController extends BaseController
{
    @Autowired
    private IMeteoWeatherDailyService meteoWeatherDailyService;

    /**
     * 查询weatherDaily列表
     */
    @PreAuthorize("@ss.hasPermi('weather:daily:list')")
    @GetMapping("/list")
    public TableDataInfo list(MeteoWeatherDaily meteoWeatherDaily)
    {
        startPage();
        List<MeteoWeatherDaily> list = meteoWeatherDailyService.selectMeteoWeatherDailyList(meteoWeatherDaily);
        return getDataTable(list);
    }

    /**
     * 导出weatherDaily列表
     */
    @PreAuthorize("@ss.hasPermi('weather:daily:export')")
    @Log(title = "weatherDaily", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MeteoWeatherDaily meteoWeatherDaily)
    {
        List<MeteoWeatherDaily> list = meteoWeatherDailyService.selectMeteoWeatherDailyList(meteoWeatherDaily);
        ExcelUtil<MeteoWeatherDaily> util = new ExcelUtil<MeteoWeatherDaily>(MeteoWeatherDaily.class);
        util.exportExcel(response, list, "weatherDaily数据");
    }

    /**
     * 获取weatherDaily详细信息
     */
    @PreAuthorize("@ss.hasPermi('weather:daily:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(meteoWeatherDailyService.selectMeteoWeatherDailyById(id));
    }

    /**
     * 新增weatherDaily
     */
    @PreAuthorize("@ss.hasPermi('weather:daily:add')")
    @Log(title = "weatherDaily", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MeteoWeatherDaily meteoWeatherDaily)
    {
        return toAjax(meteoWeatherDailyService.insertMeteoWeatherDaily(meteoWeatherDaily));
    }

    /**
     * 修改weatherDaily
     */
    @PreAuthorize("@ss.hasPermi('weather:daily:edit')")
    @Log(title = "weatherDaily", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MeteoWeatherDaily meteoWeatherDaily)
    {
        return toAjax(meteoWeatherDailyService.updateMeteoWeatherDaily(meteoWeatherDaily));
    }

    /**
     * 删除weatherDaily
     */
    @PreAuthorize("@ss.hasPermi('weather:daily:remove')")
    @Log(title = "weatherDaily", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(meteoWeatherDailyService.deleteMeteoWeatherDailyByIds(ids));
    }
}
