package com.ruoyi.transport.controller;

import java.util.List;
import java.util.Map;
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
import com.ruoyi.transport.domain.TransportDemand;
import com.ruoyi.transport.service.ITransportDemandService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.bind.annotation.RequestParam; // ✅ 解决 RequestParam 报错
import com.ruoyi.transport.domain.TransportDemandWithCity;  // ✅ 导入 TransportDemandWithCity


/**
 * 物流发布Controller 我没删
 * 
 * @author dt
 * @date 2025-06-25
 */
@RestController
@RequestMapping("/transport/publish")
public class TransportDemandController extends BaseController
{
    @Autowired
    private ITransportDemandService transportDemandService;

    /**
     * 查询物流发布列表
     */
    @PreAuthorize("@ss.hasPermi('transport:publish:list')")
    @GetMapping("/list")
    public TableDataInfo list(TransportDemand transportDemand)
    {
        startPage();
        List<TransportDemand> list = transportDemandService.selectTransportDemandList(transportDemand);
        return getDataTable(list);
    }

//
//    /**
//     * 查询物流发布列表,返回带城市的货物信息
//     */
//    @GetMapping("/listWithCity")
//    public List<TransportDemandWithCity> listWithCity(@RequestParam Map<String, Object> params) {
//        return transportDemandService.selectPublishedVesselToCargoWithCities(params);
//    }

//    /**
//     * 查询返回带城市的货物信息
//     */
//    @PreAuthorize("@ss.hasPermi('transport:publish:list')")
//    @GetMapping("/listWithCity")
//    public TableDataInfo listWithCity(@RequestParam Map<String, Object> params)
//    {
//        // 开启分页 (pageNum、pageSize 从请求参数中读取)
//        startPage();
//        // 按 Map 参数调用 service 查询
//        List<TransportDemandWithCity> list = transportDemandService
//                .selectPublishedVesselToCargoWithCities(params);
//        // 封装为 TableDataInfo 返回
//        return getDataTable(list);
//    }


    /**
     * 导出物流发布列表
     */
    @PreAuthorize("@ss.hasPermi('transport:publish:export')")
    @Log(title = "物流发布", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TransportDemand transportDemand)
    {
        List<TransportDemand> list = transportDemandService.selectTransportDemandList(transportDemand);
        ExcelUtil<TransportDemand> util = new ExcelUtil<TransportDemand>(TransportDemand.class);
        util.exportExcel(response, list, "物流发布数据");
    }

    /**
     * 获取物流发布详细信息
     */
    @PreAuthorize("@ss.hasPermi('transport:publish:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(transportDemandService.selectTransportDemandById(id));
    }

    /**
     * 新增物流发布
     */
    @PreAuthorize("@ss.hasPermi('transport:publish:add')")
    @Log(title = "物流发布", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TransportDemand transportDemand)
    {
        return toAjax(transportDemandService.insertTransportDemand(transportDemand));
    }

    /**
     * 修改物流发布
     */
    @PreAuthorize("@ss.hasPermi('transport:publish:edit')")
    @Log(title = "物流发布", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TransportDemand transportDemand)
    {
        return toAjax(transportDemandService.updateTransportDemand(transportDemand));
    }

    /**
     * 删除物流发布
     */
    @PreAuthorize("@ss.hasPermi('transport:publish:remove')")
    @Log(title = "物流发布", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(transportDemandService.deleteTransportDemandByIds(ids));
    }
}
