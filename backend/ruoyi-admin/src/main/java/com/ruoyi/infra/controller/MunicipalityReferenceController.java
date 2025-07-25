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
import com.ruoyi.infra.domain.MunicipalityReference;
import com.ruoyi.infra.service.IMunicipalityReferenceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * MunicipalityReferenceTableController
 * 
 * @author dt
 * @date 2025-06-28
 */
@RestController
@RequestMapping("/infrastructure/municipalityReference")
public class MunicipalityReferenceController extends BaseController
{
    @Autowired
    private IMunicipalityReferenceService municipalityReferenceService;

    /**
     * 查询MunicipalityReferenceTable列表
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:municipalityReference:list')")
    @GetMapping("/list")
    public TableDataInfo list(MunicipalityReference municipalityReference)
    {
        startPage();
        List<MunicipalityReference> list = municipalityReferenceService.selectMunicipalityReferenceList(municipalityReference);
        return getDataTable(list);
    }

    /**
     * 导出MunicipalityReferenceTable列表
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:municipalityReference:export')")
    @Log(title = "MunicipalityReferenceTable", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MunicipalityReference municipalityReference)
    {
        List<MunicipalityReference> list = municipalityReferenceService.selectMunicipalityReferenceList(municipalityReference);
        ExcelUtil<MunicipalityReference> util = new ExcelUtil<MunicipalityReference>(MunicipalityReference.class);
        util.exportExcel(response, list, "MunicipalityReferenceTable数据");
    }

    /**
     * 获取MunicipalityReferenceTable详细信息
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:municipalityReference:query')")
    @GetMapping(value = "/{municipalityCode}")
    public AjaxResult getInfo(@PathVariable("municipalityCode") String municipalityCode)
    {
        return success(municipalityReferenceService.selectMunicipalityReferenceByMunicipalityCode(municipalityCode));
    }

    /**
     * 新增MunicipalityReferenceTable
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:municipalityReference:add')")
    @Log(title = "MunicipalityReferenceTable", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MunicipalityReference municipalityReference)
    {
        return toAjax(municipalityReferenceService.insertMunicipalityReference(municipalityReference));
    }

    /**
     * 修改MunicipalityReferenceTable
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:municipalityReference:edit')")
    @Log(title = "MunicipalityReferenceTable", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MunicipalityReference municipalityReference)
    {
        return toAjax(municipalityReferenceService.updateMunicipalityReference(municipalityReference));
    }

    /**
     * 删除MunicipalityReferenceTable
     */
    @PreAuthorize("@ss.hasPermi('infrastructure:municipalityReference:remove')")
    @Log(title = "MunicipalityReferenceTable", businessType = BusinessType.DELETE)
	@DeleteMapping("/{municipalityCodes}")
    public AjaxResult remove(@PathVariable String[] municipalityCodes)
    {
        return toAjax(municipalityReferenceService.deleteMunicipalityReferenceByMunicipalityCodes(municipalityCodes));
    }
}
