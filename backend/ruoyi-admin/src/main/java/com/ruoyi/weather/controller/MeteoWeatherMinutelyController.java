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
import com.ruoyi.weather.domain.MeteoWeatherMinutely;
import com.ruoyi.weather.service.IMeteoWeatherMinutelyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * weatherMinutelyController
 * 
 * @author dt
 * @date 2025-07-03
 */
@RestController
@RequestMapping("/weather/Minutely")
public class MeteoWeatherMinutelyController extends BaseController
{
    @Autowired
    private IMeteoWeatherMinutelyService meteoWeatherMinutelyService;

    /**
     * 查询weatherMinutely列表
     */
    @PreAuthorize("@ss.hasPermi('weather:Minutely:list')")
    @GetMapping("/list")
    public TableDataInfo list(MeteoWeatherMinutely meteoWeatherMinutely)
    {
        startPage();
        List<MeteoWeatherMinutely> list = meteoWeatherMinutelyService.selectMeteoWeatherMinutelyList(meteoWeatherMinutely);
        return getDataTable(list);
    }

    /**
     * 导出weatherMinutely列表
     */
    @PreAuthorize("@ss.hasPermi('weather:Minutely:export')")
    @Log(title = "weatherMinutely", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MeteoWeatherMinutely meteoWeatherMinutely)
    {
        List<MeteoWeatherMinutely> list = meteoWeatherMinutelyService.selectMeteoWeatherMinutelyList(meteoWeatherMinutely);
        ExcelUtil<MeteoWeatherMinutely> util = new ExcelUtil<MeteoWeatherMinutely>(MeteoWeatherMinutely.class);
        util.exportExcel(response, list, "weatherMinutely数据");
    }

    /**
     * 获取weatherMinutely详细信息
     */
    @PreAuthorize("@ss.hasPermi('weather:Minutely:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(meteoWeatherMinutelyService.selectMeteoWeatherMinutelyById(id));
    }

    /**
     * 新增weatherMinutely
     */
    @PreAuthorize("@ss.hasPermi('weather:Minutely:add')")
    @Log(title = "weatherMinutely", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MeteoWeatherMinutely meteoWeatherMinutely)
    {
        return toAjax(meteoWeatherMinutelyService.insertMeteoWeatherMinutely(meteoWeatherMinutely));
    }

    /**
     * 修改weatherMinutely
     */
    @PreAuthorize("@ss.hasPermi('weather:Minutely:edit')")
    @Log(title = "weatherMinutely", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MeteoWeatherMinutely meteoWeatherMinutely)
    {
        return toAjax(meteoWeatherMinutelyService.updateMeteoWeatherMinutely(meteoWeatherMinutely));
    }

    /**
     * 删除weatherMinutely
     */
    @PreAuthorize("@ss.hasPermi('weather:Minutely:remove')")
    @Log(title = "weatherMinutely", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(meteoWeatherMinutelyService.deleteMeteoWeatherMinutelyByIds(ids));
    }
}
