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
import com.ruoyi.infra.domain.RwsVndsBerth;
import com.ruoyi.infra.service.IRwsVndsBerthService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * VNDS-berthController
 * 
 * @author dt
 * @date 2025-07-25
 */
@RestController
@RequestMapping("/infrastructure/VNDSberth")
public class RwsVndsBerthController extends BaseController
{
    @Autowired
    private IRwsVndsBerthService rwsVndsBerthService;

    /**
     * 查询VNDS-berth列表
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSberth:list')")
    @GetMapping("/list")
    public TableDataInfo list(RwsVndsBerth rwsVndsBerth)
    {
        startPage();
        List<RwsVndsBerth> list = rwsVndsBerthService.selectRwsVndsBerthList(rwsVndsBerth);
        return getDataTable(list);
    }

    /**
     * 导出VNDS-berth列表
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSberth:export')")
    @Log(title = "VNDS-berth", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RwsVndsBerth rwsVndsBerth)
    {
        List<RwsVndsBerth> list = rwsVndsBerthService.selectRwsVndsBerthList(rwsVndsBerth);
        ExcelUtil<RwsVndsBerth> util = new ExcelUtil<RwsVndsBerth>(RwsVndsBerth.class);
        util.exportExcel(response, list, "VNDS-berth数据");
    }

    /**
     * 获取VNDS-berth详细信息
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSberth:query')")
    @GetMapping(value = "/{Id}")
    public AjaxResult getInfo(@PathVariable("Id") Long Id)
    {
        return success(rwsVndsBerthService.selectRwsVndsBerthById(Id));
    }

    /**
     * 新增VNDS-berth
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSberth:add')")
    @Log(title = "VNDS-berth", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RwsVndsBerth rwsVndsBerth)
    {
        return toAjax(rwsVndsBerthService.insertRwsVndsBerth(rwsVndsBerth));
    }

    /**
     * 修改VNDS-berth
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSberth:edit')")
    @Log(title = "VNDS-berth", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RwsVndsBerth rwsVndsBerth)
    {
        return toAjax(rwsVndsBerthService.updateRwsVndsBerth(rwsVndsBerth));
    }

    /**
     * 删除VNDS-berth
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSberth:remove')")
    @Log(title = "VNDS-berth", businessType = BusinessType.DELETE)
	@DeleteMapping("/{Ids}")
    public AjaxResult remove(@PathVariable Long[] Ids)
    {
        return toAjax(rwsVndsBerthService.deleteRwsVndsBerthByIds(Ids));
    }
}
