package com.ruoyi.infra.controller;

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
import com.ruoyi.infra.domain.Unlocode;
import com.ruoyi.infra.service.IUnlocodeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * UNLOCODEController
 * 
 * @author dt
 * @date 2025-06-28
 */
@RestController
@RequestMapping("/infrastructure/unlocode")
public class UnlocodeController extends BaseController
{
    @Autowired
    private IUnlocodeService unlocodeService;

    /**
     * 查询UNLOCODE列表
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:unlocode:list')")
    @GetMapping("/list")
    public TableDataInfo list(Unlocode unlocode)
    {
        startPage();
        List<Unlocode> list = unlocodeService.selectUnlocodeList(unlocode);
        return getDataTable(list);
    }

    /**
     * 导出UNLOCODE列表
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:unlocode:export')")
    @Log(title = "UNLOCODE", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Unlocode unlocode)
    {
        List<Unlocode> list = unlocodeService.selectUnlocodeList(unlocode);
        ExcelUtil<Unlocode> util = new ExcelUtil<Unlocode>(Unlocode.class);
        util.exportExcel(response, list, "UNLOCODE数据");
    }

    /**
     * 获取UNLOCODE详细信息
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:unlocode:query')")
    @GetMapping(value = "/{locode}")
    public AjaxResult getInfo(@PathVariable("locode") String locode)
    {
        return success(unlocodeService.selectUnlocodeByLocode(locode));
    }

    /**
     * 新增UNLOCODE
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:unlocode:add')")
    @Log(title = "UNLOCODE", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Unlocode unlocode)
    {
        return toAjax(unlocodeService.insertUnlocode(unlocode));
    }

    /**
     * 修改UNLOCODE
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:unlocode:edit')")
    @Log(title = "UNLOCODE", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Unlocode unlocode)
    {
        return toAjax(unlocodeService.updateUnlocode(unlocode));
    }

    /**
     * 删除UNLOCODE
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:unlocode:remove')")
    @Log(title = "UNLOCODE", businessType = BusinessType.DELETE)
	@DeleteMapping("/{locodes}")
    public AjaxResult remove(@PathVariable String[] locodes)
    {
        return toAjax(unlocodeService.deleteUnlocodeByLocodes(locodes));
    }
}
