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
import com.ruoyi.infra.domain.BerthInfo;
import com.ruoyi.infra.service.IBerthInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * berthInfoController
 * 
 * @author dt
 * @date 2025-06-26
 */
@RestController
@RequestMapping("/infrastructure/berth")
public class BerthInfoController extends BaseController
{
    @Autowired
    private IBerthInfoService berthInfoService;

    /**
     * 查询berthInfo列表
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:berth:list')")
    @GetMapping("/list")
    public TableDataInfo list(BerthInfo berthInfo)
    {
        startPage();
        List<BerthInfo> list = berthInfoService.selectBerthInfoList(berthInfo);
        return getDataTable(list);
    }

    /**
     * 导出berthInfo列表
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:berth:export')")
    @Log(title = "berthInfo", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BerthInfo berthInfo)
    {
        List<BerthInfo> list = berthInfoService.selectBerthInfoList(berthInfo);
        ExcelUtil<BerthInfo> util = new ExcelUtil<BerthInfo>(BerthInfo.class);
        util.exportExcel(response, list, "berthInfo数据");
    }

    /**
     * 获取berthInfo详细信息
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:berth:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(berthInfoService.selectBerthInfoById(id));
    }

    /**
     * 新增berthInfo
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:berth:add')")
    @Log(title = "berthInfo", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BerthInfo berthInfo)
    {
        return toAjax(berthInfoService.insertBerthInfo(berthInfo));
    }

    /**
     * 修改berthInfo
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:berth:edit')")
    @Log(title = "berthInfo", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BerthInfo berthInfo)
    {
        return toAjax(berthInfoService.updateBerthInfo(berthInfo));
    }

    /**
     * 删除berthInfo
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:berth:remove')")
    @Log(title = "berthInfo", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(berthInfoService.deleteBerthInfoByIds(ids));
    }
}
