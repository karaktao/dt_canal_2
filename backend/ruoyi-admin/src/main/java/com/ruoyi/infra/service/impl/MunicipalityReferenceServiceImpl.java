package com.ruoyi.infra.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infra.mapper.MunicipalityReferenceMapper;
import com.ruoyi.infra.domain.MunicipalityReference;
import com.ruoyi.infra.service.IMunicipalityReferenceService;

/**
 * MunicipalityReferenceTableService业务层处理
 * 
 * @author dt
 * @date 2025-06-28
 */
@Service
public class MunicipalityReferenceServiceImpl implements IMunicipalityReferenceService 
{
    @Autowired
    private MunicipalityReferenceMapper municipalityReferenceMapper;

    /**
     * 查询MunicipalityReferenceTable
     * 
     * @param municipalityCode MunicipalityReferenceTable主键
     * @return MunicipalityReferenceTable
     */
    @Override
    public MunicipalityReference selectMunicipalityReferenceByMunicipalityCode(String municipalityCode)
    {
        return municipalityReferenceMapper.selectMunicipalityReferenceByMunicipalityCode(municipalityCode);
    }

    /**
     * 查询MunicipalityReferenceTable列表
     * 
     * @param municipalityReference MunicipalityReferenceTable
     * @return MunicipalityReferenceTable
     */
    @Override
    public List<MunicipalityReference> selectMunicipalityReferenceList(MunicipalityReference municipalityReference)
    {
        return municipalityReferenceMapper.selectMunicipalityReferenceList(municipalityReference);
    }

    /**
     * 新增MunicipalityReferenceTable
     * 
     * @param municipalityReference MunicipalityReferenceTable
     * @return 结果
     */
    @Override
    public int insertMunicipalityReference(MunicipalityReference municipalityReference)
    {
        return municipalityReferenceMapper.insertMunicipalityReference(municipalityReference);
    }

    /**
     * 修改MunicipalityReferenceTable
     * 
     * @param municipalityReference MunicipalityReferenceTable
     * @return 结果
     */
    @Override
    public int updateMunicipalityReference(MunicipalityReference municipalityReference)
    {
        return municipalityReferenceMapper.updateMunicipalityReference(municipalityReference);
    }

    /**
     * 批量删除MunicipalityReferenceTable
     * 
     * @param municipalityCodes 需要删除的MunicipalityReferenceTable主键
     * @return 结果
     */
    @Override
    public int deleteMunicipalityReferenceByMunicipalityCodes(String[] municipalityCodes)
    {
        return municipalityReferenceMapper.deleteMunicipalityReferenceByMunicipalityCodes(municipalityCodes);
    }

    /**
     * 删除MunicipalityReferenceTable信息
     * 
     * @param municipalityCode MunicipalityReferenceTable主键
     * @return 结果
     */
    @Override
    public int deleteMunicipalityReferenceByMunicipalityCode(String municipalityCode)
    {
        return municipalityReferenceMapper.deleteMunicipalityReferenceByMunicipalityCode(municipalityCode);
    }
}
