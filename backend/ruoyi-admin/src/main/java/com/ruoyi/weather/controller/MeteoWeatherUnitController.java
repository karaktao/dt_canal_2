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
import com.ruoyi.weather.domain.MeteoWeatherUnit;
import com.ruoyi.weather.service.impl.IMeteoWeatherUnitService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * weatherUnitController
 * 
 * @author dt
 * @date 2025-07-03
 */
@RestController
@RequestMapping("/weather/unit")
public class MeteoWeatherUnitController extends BaseController
{
    @Autowired
    private IMeteoWeatherUnitService meteoWeatherUnitService;

    /**
     * 查询weatherUnit列表
     */
    @PreAuthorize("@ss.hasPermi('weather:unit:list')")
    @GetMapping("/list")
    public TableDataInfo list(MeteoWeatherUnit meteoWeatherUnit)
    {
        startPage();
        List<MeteoWeatherUnit> list = meteoWeatherUnitService.selectMeteoWeatherUnitList(meteoWeatherUnit);
        return getDataTable(list);
    }

    /**
     * 导出weatherUnit列表
     */
    @PreAuthorize("@ss.hasPermi('weather:unit:export')")
    @Log(title = "weatherUnit", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MeteoWeatherUnit meteoWeatherUnit)
    {
        List<MeteoWeatherUnit> list = meteoWeatherUnitService.selectMeteoWeatherUnitList(meteoWeatherUnit);
        ExcelUtil<MeteoWeatherUnit> util = new ExcelUtil<MeteoWeatherUnit>(MeteoWeatherUnit.class);
        util.exportExcel(response, list, "weatherUnit数据");
    }

    /**
     * 获取weatherUnit详细信息
     */
    @PreAuthorize("@ss.hasPermi('weather:unit:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(meteoWeatherUnitService.selectMeteoWeatherUnitById(id));
    }

    /**
     * 新增weatherUnit
     */
    @PreAuthorize("@ss.hasPermi('weather:unit:add')")
    @Log(title = "weatherUnit", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MeteoWeatherUnit meteoWeatherUnit)
    {
        return toAjax(meteoWeatherUnitService.insertMeteoWeatherUnit(meteoWeatherUnit));
    }

    /**
     * 修改weatherUnit
     */
    @PreAuthorize("@ss.hasPermi('weather:unit:edit')")
    @Log(title = "weatherUnit", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MeteoWeatherUnit meteoWeatherUnit)
    {
        return toAjax(meteoWeatherUnitService.updateMeteoWeatherUnit(meteoWeatherUnit));
    }

    /**
     * 删除weatherUnit
     */
    @PreAuthorize("@ss.hasPermi('weather:unit:remove')")
    @Log(title = "weatherUnit", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(meteoWeatherUnitService.deleteMeteoWeatherUnitByIds(ids));
    }
}
