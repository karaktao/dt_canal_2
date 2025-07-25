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
import com.ruoyi.weather.domain.MeteoWeatherCurrent;
import com.ruoyi.weather.service.IMeteoWeatherCurrentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * weatherCurrentController
 * 
 * @author dt
 * @date 2025-07-03
 */
@RestController
@RequestMapping("/weather/current")
public class MeteoWeatherCurrentController extends BaseController
{
    @Autowired
    private IMeteoWeatherCurrentService meteoWeatherCurrentService;

    /**
     * 查询weatherCurrent列表
     */
    @PreAuthorize("@ss.hasPermi('weather:current:list')")
    @GetMapping("/list")
    public TableDataInfo list(MeteoWeatherCurrent meteoWeatherCurrent)
    {
        startPage();
        List<MeteoWeatherCurrent> list = meteoWeatherCurrentService.selectMeteoWeatherCurrentList(meteoWeatherCurrent);
        return getDataTable(list);
    }

    /**
     * 导出weatherCurrent列表
     */
    @PreAuthorize("@ss.hasPermi('weather:current:export')")
    @Log(title = "weatherCurrent", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MeteoWeatherCurrent meteoWeatherCurrent)
    {
        List<MeteoWeatherCurrent> list = meteoWeatherCurrentService.selectMeteoWeatherCurrentList(meteoWeatherCurrent);
        ExcelUtil<MeteoWeatherCurrent> util = new ExcelUtil<MeteoWeatherCurrent>(MeteoWeatherCurrent.class);
        util.exportExcel(response, list, "weatherCurrent数据");
    }

    /**
     * 获取weatherCurrent详细信息
     */
    @PreAuthorize("@ss.hasPermi('weather:current:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(meteoWeatherCurrentService.selectMeteoWeatherCurrentById(id));
    }

    /**
     * 新增weatherCurrent
     */
    @PreAuthorize("@ss.hasPermi('weather:current:add')")
    @Log(title = "weatherCurrent", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MeteoWeatherCurrent meteoWeatherCurrent)
    {
        return toAjax(meteoWeatherCurrentService.insertMeteoWeatherCurrent(meteoWeatherCurrent));
    }

    /**
     * 修改weatherCurrent
     */
    @PreAuthorize("@ss.hasPermi('weather:current:edit')")
    @Log(title = "weatherCurrent", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MeteoWeatherCurrent meteoWeatherCurrent)
    {
        return toAjax(meteoWeatherCurrentService.updateMeteoWeatherCurrent(meteoWeatherCurrent));
    }

    /**
     * 删除weatherCurrent
     */
    @PreAuthorize("@ss.hasPermi('weather:current:remove')")
    @Log(title = "weatherCurrent", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(meteoWeatherCurrentService.deleteMeteoWeatherCurrentByIds(ids));
    }
}
