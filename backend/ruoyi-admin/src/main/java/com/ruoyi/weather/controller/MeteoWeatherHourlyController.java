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
import com.ruoyi.weather.domain.MeteoWeatherHourly;
import com.ruoyi.weather.service.IMeteoWeatherHourlyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * weatherHourlyController
 * 
 * @author dt
 * @date 2025-07-03
 */
@RestController
@RequestMapping("/weather/hourly")
public class MeteoWeatherHourlyController extends BaseController
{
    @Autowired
    private IMeteoWeatherHourlyService meteoWeatherHourlyService;

    /**
     * 查询weatherHourly列表
     */
    @PreAuthorize("@ss.hasPermi('weather:hourly:list')")
    @GetMapping("/list")
    public TableDataInfo list(MeteoWeatherHourly meteoWeatherHourly)
    {
        startPage();
        List<MeteoWeatherHourly> list = meteoWeatherHourlyService.selectMeteoWeatherHourlyList(meteoWeatherHourly);
        return getDataTable(list);
    }

    /**
     * 导出weatherHourly列表
     */
    @PreAuthorize("@ss.hasPermi('weather:hourly:export')")
    @Log(title = "weatherHourly", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MeteoWeatherHourly meteoWeatherHourly)
    {
        List<MeteoWeatherHourly> list = meteoWeatherHourlyService.selectMeteoWeatherHourlyList(meteoWeatherHourly);
        ExcelUtil<MeteoWeatherHourly> util = new ExcelUtil<MeteoWeatherHourly>(MeteoWeatherHourly.class);
        util.exportExcel(response, list, "weatherHourly数据");
    }

    /**
     * 获取weatherHourly详细信息
     */
    @PreAuthorize("@ss.hasPermi('weather:hourly:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(meteoWeatherHourlyService.selectMeteoWeatherHourlyById(id));
    }

    /**
     * 新增weatherHourly
     */
    @PreAuthorize("@ss.hasPermi('weather:hourly:add')")
    @Log(title = "weatherHourly", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MeteoWeatherHourly meteoWeatherHourly)
    {
        return toAjax(meteoWeatherHourlyService.insertMeteoWeatherHourly(meteoWeatherHourly));
    }

    /**
     * 修改weatherHourly
     */
    @PreAuthorize("@ss.hasPermi('weather:hourly:edit')")
    @Log(title = "weatherHourly", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MeteoWeatherHourly meteoWeatherHourly)
    {
        return toAjax(meteoWeatherHourlyService.updateMeteoWeatherHourly(meteoWeatherHourly));
    }

    /**
     * 删除weatherHourly
     */
    @PreAuthorize("@ss.hasPermi('weather:hourly:remove')")
    @Log(title = "weatherHourly", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(meteoWeatherHourlyService.deleteMeteoWeatherHourlyByIds(ids));
    }
}
