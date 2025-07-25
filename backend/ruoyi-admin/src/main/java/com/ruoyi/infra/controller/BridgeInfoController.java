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
import com.ruoyi.infra.domain.BridgeInfo;
import com.ruoyi.infra.service.IBridgeInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * bridgeinfoController
 * 
 * @author dt
 * @date 2025-06-26
 */
@RestController
@RequestMapping("/infrastructure/bridge")
public class BridgeInfoController extends BaseController
{
    @Autowired
    private IBridgeInfoService bridgeInfoService;

    /**
     * 查询bridgeinfo列表
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:bridge:list')")
    @GetMapping("/list")
    public TableDataInfo list(BridgeInfo bridgeInfo)
    {
        startPage();
        List<BridgeInfo> list = bridgeInfoService.selectBridgeInfoList(bridgeInfo);
        return getDataTable(list);
    }

    /**
     * 导出bridgeinfo列表
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:bridge:export')")
    @Log(title = "bridgeinfo", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BridgeInfo bridgeInfo)
    {
        List<BridgeInfo> list = bridgeInfoService.selectBridgeInfoList(bridgeInfo);
        ExcelUtil<BridgeInfo> util = new ExcelUtil<BridgeInfo>(BridgeInfo.class);
        util.exportExcel(response, list, "bridgeinfo数据");
    }

    /**
     * 获取bridgeinfo详细信息
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:bridge:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bridgeInfoService.selectBridgeInfoById(id));
    }

    /**
     * 新增bridgeinfo
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:bridge:add')")
    @Log(title = "bridgeinfo", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BridgeInfo bridgeInfo)
    {
        return toAjax(bridgeInfoService.insertBridgeInfo(bridgeInfo));
    }

    /**
     * 修改bridgeinfo
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:bridge:edit')")
    @Log(title = "bridgeinfo", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BridgeInfo bridgeInfo)
    {
        return toAjax(bridgeInfoService.updateBridgeInfo(bridgeInfo));
    }

    /**
     * 删除bridgeinfo
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:bridge:remove')")
    @Log(title = "bridgeinfo", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bridgeInfoService.deleteBridgeInfoByIds(ids));
    }
}
