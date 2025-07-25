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
import com.ruoyi.infra.domain.RwsVndsLock;
import com.ruoyi.infra.service.IRwsVndsLockService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * VNDSlockController
 * 
 * @author dt
 * @date 2025-07-25
 */
@RestController
@RequestMapping("/infrastructure/VNDSlock")
public class RwsVndsLockController extends BaseController
{
    @Autowired
    private IRwsVndsLockService rwsVndsLockService;

    /**
     * 查询VNDSlock列表
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSlock:list')")
    @GetMapping("/list")
    public TableDataInfo list(RwsVndsLock rwsVndsLock)
    {
        startPage();
        List<RwsVndsLock> list = rwsVndsLockService.selectRwsVndsLockList(rwsVndsLock);
        return getDataTable(list);
    }

    /**
     * 导出VNDSlock列表
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSlock:export')")
    @Log(title = "VNDSlock", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RwsVndsLock rwsVndsLock)
    {
        List<RwsVndsLock> list = rwsVndsLockService.selectRwsVndsLockList(rwsVndsLock);
        ExcelUtil<RwsVndsLock> util = new ExcelUtil<RwsVndsLock>(RwsVndsLock.class);
        util.exportExcel(response, list, "VNDSlock数据");
    }

    /**
     * 获取VNDSlock详细信息
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSlock:query')")
    @GetMapping(value = "/{Id}")
    public AjaxResult getInfo(@PathVariable("Id") Long Id)
    {
        return success(rwsVndsLockService.selectRwsVndsLockById(Id));
    }

    /**
     * 新增VNDSlock
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSlock:add')")
    @Log(title = "VNDSlock", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RwsVndsLock rwsVndsLock)
    {
        return toAjax(rwsVndsLockService.insertRwsVndsLock(rwsVndsLock));
    }

    /**
     * 修改VNDSlock
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSlock:edit')")
    @Log(title = "VNDSlock", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RwsVndsLock rwsVndsLock)
    {
        return toAjax(rwsVndsLockService.updateRwsVndsLock(rwsVndsLock));
    }

    /**
     * 删除VNDSlock
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSlock:remove')")
    @Log(title = "VNDSlock", businessType = BusinessType.DELETE)
	@DeleteMapping("/{Ids}")
    public AjaxResult remove(@PathVariable Long[] Ids)
    {
        return toAjax(rwsVndsLockService.deleteRwsVndsLockByIds(Ids));
    }
}
