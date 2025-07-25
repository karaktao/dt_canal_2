package com.ruoyi.infra.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.infra.domain.RwsCatalogus;
import com.ruoyi.infra.service.IRwsCatalogusService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * rwsCatalogusController
 * 
 * @author dt
 * @date 2025-07-07
 */
@RestController
@RequestMapping("/infrastructure/rwsCatalogus")
public class RwsCatalogusController extends BaseController
{
    @Autowired
    private IRwsCatalogusService rwsCatalogusService;

    /**
     * 查询rwsCatalogus列表
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:rwsCatalogus:list')")
    @GetMapping("/list")
    public TableDataInfo list(RwsCatalogus rwsCatalogus)
    {
        startPage();
        List<RwsCatalogus> list = rwsCatalogusService.selectRwsCatalogusList(rwsCatalogus);
        return getDataTable(list);
    }


    /**
     * 导出rwsCatalogus列表
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:rwsCatalogus:export')")
    @Log(title = "rwsCatalogus", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RwsCatalogus rwsCatalogus)
    {
        List<RwsCatalogus> list = rwsCatalogusService.selectRwsCatalogusList(rwsCatalogus);
        ExcelUtil<RwsCatalogus> util = new ExcelUtil<RwsCatalogus>(RwsCatalogus.class);
        util.exportExcel(response, list, "rwsCatalogus数据");
    }

    /**
     * 获取rwsCatalogus详细信息
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:rwsCatalogus:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(rwsCatalogusService.selectRwsCatalogusById(id));
    }

    /**
     * 新增rwsCatalogus
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:rwsCatalogus:add')")
    @Log(title = "rwsCatalogus", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RwsCatalogus rwsCatalogus)
    {
        return toAjax(rwsCatalogusService.insertRwsCatalogus(rwsCatalogus));
    }

    /**
     * 修改rwsCatalogus
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:rwsCatalogus:edit')")
    @Log(title = "rwsCatalogus", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RwsCatalogus rwsCatalogus)
    {
        return toAjax(rwsCatalogusService.updateRwsCatalogus(rwsCatalogus));
    }

    /**
     * 删除rwsCatalogus
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:rwsCatalogus:remove')")
    @Log(title = "rwsCatalogus", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(rwsCatalogusService.deleteRwsCatalogusByIds(ids));
    }
}
