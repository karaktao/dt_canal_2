package com.ruoyi.infra.service;

import java.util.List;
import com.ruoyi.infra.domain.MunicipalityReference;

/**
 * MunicipalityReferenceTableService接口
 * 
 * @author dt
 * @date 2025-06-28
 */
public interface IMunicipalityReferenceService 
{
    /**
     * 查询MunicipalityReferenceTable
     * 
     * @param municipalityCode MunicipalityReferenceTable主键
     * @return MunicipalityReferenceTable
     */
    public MunicipalityReference selectMunicipalityReferenceByMunicipalityCode(String municipalityCode);

    /**
     * 查询MunicipalityReferenceTable列表
     * 
     * @param municipalityReference MunicipalityReferenceTable
     * @return MunicipalityReferenceTable集合
     */
    public List<MunicipalityReference> selectMunicipalityReferenceList(MunicipalityReference municipalityReference);

    /**
     * 新增MunicipalityReferenceTable
     * 
     * @param municipalityReference MunicipalityReferenceTable
     * @return 结果
     */
    public int insertMunicipalityReference(MunicipalityReference municipalityReference);

    /**
     * 修改MunicipalityReferenceTable
     * 
     * @param municipalityReference MunicipalityReferenceTable
     * @return 结果
     */
    public int updateMunicipalityReference(MunicipalityReference municipalityReference);

    /**
     * 批量删除MunicipalityReferenceTable
     * 
     * @param municipalityCodes 需要删除的MunicipalityReferenceTable主键集合
     * @return 结果
     */
    public int deleteMunicipalityReferenceByMunicipalityCodes(String[] municipalityCodes);

    /**
     * 删除MunicipalityReferenceTable信息
     * 
     * @param municipalityCode MunicipalityReferenceTable主键
     * @return 结果
     */
    public int deleteMunicipalityReferenceByMunicipalityCode(String municipalityCode);
}
