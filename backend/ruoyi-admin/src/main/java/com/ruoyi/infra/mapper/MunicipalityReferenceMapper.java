package com.ruoyi.infra.mapper;

import java.util.List;
import com.ruoyi.infra.domain.MunicipalityReference;

/**
 * MunicipalityReferenceTableMapper接口
 * 
 * @author dt
 * @date 2025-06-28
 */
public interface MunicipalityReferenceMapper 
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
     * 删除MunicipalityReferenceTable
     * 
     * @param municipalityCode MunicipalityReferenceTable主键
     * @return 结果
     */
    public int deleteMunicipalityReferenceByMunicipalityCode(String municipalityCode);

    /**
     * 批量删除MunicipalityReferenceTable
     * 
     * @param municipalityCodes 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMunicipalityReferenceByMunicipalityCodes(String[] municipalityCodes);
}
