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
import com.ruoyi.infra.domain.LockInfo;
import com.ruoyi.infra.service.ILockInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * lockinfoController
 * 
 * @author dt
 * @date 2025-06-26
 */
@RestController
@RequestMapping("/infrastructure/lock")
public class LockInfoController extends BaseController
{
    @Autowired
    private ILockInfoService lockInfoService;

    /**
     * 查询lockinfo列表
     */
//    @PreAuthorize("@ss.hasPermi('infrastructure:lock:list')")
    @GetMapping("/list")
    public TableDataInfo list(LockInfo lockInfo)
    {
        startPage();
        List<LockInfo> list = lockInfoService.selectLockInfoList(lockInfo);
        return getDataTable(list);
    }

    /**
     * 导出lockinfo列表
     */
//    @PreAuthorize("@ss.hasPermi('infrastructure:lock:export')")
    @Log(title = "lockinfo", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LockInfo lockInfo)
    {
        List<LockInfo> list = lockInfoService.selectLockInfoList(lockInfo);
        ExcelUtil<LockInfo> util = new ExcelUtil<LockInfo>(LockInfo.class);
        util.exportExcel(response, list, "lockinfo数据");
    }

    /**
     * 获取lockinfo详细信息
     */
//    @PreAuthorize("@ss.hasPermi('infrastructure:lock:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(lockInfoService.selectLockInfoById(id));
    }

    /**
     * 新增lockinfo
     */
//    @PreAuthorize("@ss.hasPermi('infrastructure:lock:add')")
    @Log(title = "lockinfo", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LockInfo lockInfo)
    {
        return toAjax(lockInfoService.insertLockInfo(lockInfo));
    }

    /**
     * 修改lockinfo
     */
//    @PreAuthorize("@ss.hasPermi('infrastructure:lock:edit')")
    @Log(title = "lockinfo", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LockInfo lockInfo)
    {
        return toAjax(lockInfoService.updateLockInfo(lockInfo));
    }

    /**
     * 删除lockinfo
     */
//    @PreAuthorize("@ss.hasPermi('infrastructure:lock:remove')")
    @Log(title = "lockinfo", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(lockInfoService.deleteLockInfoByIds(ids));
    }
}
