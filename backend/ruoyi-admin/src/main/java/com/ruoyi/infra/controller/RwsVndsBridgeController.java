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
import com.ruoyi.infra.domain.RwsVndsBridge;
import com.ruoyi.infra.service.IRwsVndsBridgeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * VNDSbridgeController
 * 
 * @author dt
 * @date 2025-07-25
 */
@RestController
@RequestMapping("/infrastructure/VNDSbridge")
public class RwsVndsBridgeController extends BaseController
{
    @Autowired
    private IRwsVndsBridgeService rwsVndsBridgeService;

    /**
     * 查询VNDSbridge列表
     */
//    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSbridge:list')")
    @GetMapping("/list")
    public TableDataInfo list(RwsVndsBridge rwsVndsBridge)
    {
        startPage();
        List<RwsVndsBridge> list = rwsVndsBridgeService.selectRwsVndsBridgeList(rwsVndsBridge);
        return getDataTable(list);
    }

    /**
     * 导出VNDSbridge列表
     */
//    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSbridge:export')")
    @Log(title = "VNDSbridge", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RwsVndsBridge rwsVndsBridge)
    {
        List<RwsVndsBridge> list = rwsVndsBridgeService.selectRwsVndsBridgeList(rwsVndsBridge);
        ExcelUtil<RwsVndsBridge> util = new ExcelUtil<RwsVndsBridge>(RwsVndsBridge.class);
        util.exportExcel(response, list, "VNDSbridge数据");
    }

    /**
     * 获取VNDSbridge详细信息
     */
//    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSbridge:query')")
    @GetMapping(value = "/{Id}")
    public AjaxResult getInfo(@PathVariable("Id") Long Id)
    {
        return success(rwsVndsBridgeService.selectRwsVndsBridgeById(Id));
    }

    /**
     * 新增VNDSbridge
     */
//    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSbridge:add')")
    @Log(title = "VNDSbridge", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RwsVndsBridge rwsVndsBridge)
    {
        return toAjax(rwsVndsBridgeService.insertRwsVndsBridge(rwsVndsBridge));
    }

    /**
     * 修改VNDSbridge
     */
//    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSbridge:edit')")
    @Log(title = "VNDSbridge", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RwsVndsBridge rwsVndsBridge)
    {
        return toAjax(rwsVndsBridgeService.updateRwsVndsBridge(rwsVndsBridge));
    }

    /**
     * 删除VNDSbridge
     */
//    @PreAuthorize("@ss.hasPermi('infrastructure:VNDSbridge:remove')")
    @Log(title = "VNDSbridge", businessType = BusinessType.DELETE)
	@DeleteMapping("/{Ids}")
    public AjaxResult remove(@PathVariable Long[] Ids)
    {
        return toAjax(rwsVndsBridgeService.deleteRwsVndsBridgeByIds(Ids));
    }
}
