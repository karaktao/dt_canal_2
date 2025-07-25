package com.ruoyi.infra.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infra.mapper.UnlocodeMapper;
import com.ruoyi.infra.domain.Unlocode;
import com.ruoyi.infra.service.IUnlocodeService;

/**
 * UNLOCODEService业务层处理
 * 
 * @author dt
 * @date 2025-06-28
 */
@Service
public class UnlocodeServiceImpl implements IUnlocodeService 
{
    @Autowired
    private UnlocodeMapper unlocodeMapper;

    /**
     * 查询UNLOCODE
     * 
     * @param locode UNLOCODE主键
     * @return UNLOCODE
     */
    @Override
    public Unlocode selectUnlocodeByLocode(String locode)
    {
        return unlocodeMapper.selectUnlocodeByLocode(locode);
    }

    /**
     * 查询UNLOCODE列表
     * 
     * @param unlocode UNLOCODE
     * @return UNLOCODE
     */
    @Override
    public List<Unlocode> selectUnlocodeList(Unlocode unlocode)
    {
        return unlocodeMapper.selectUnlocodeList(unlocode);
    }

    /**
     * 新增UNLOCODE
     * 
     * @param unlocode UNLOCODE
     * @return 结果
     */
    @Override
    public int insertUnlocode(Unlocode unlocode)
    {
        return unlocodeMapper.insertUnlocode(unlocode);
    }

    /**
     * 修改UNLOCODE
     * 
     * @param unlocode UNLOCODE
     * @return 结果
     */
    @Override
    public int updateUnlocode(Unlocode unlocode)
    {
        return unlocodeMapper.updateUnlocode(unlocode);
    }

    /**
     * 批量删除UNLOCODE
     * 
     * @param locodes 需要删除的UNLOCODE主键
     * @return 结果
     */
    @Override
    public int deleteUnlocodeByLocodes(String[] locodes)
    {
        return unlocodeMapper.deleteUnlocodeByLocodes(locodes);
    }

    /**
     * 删除UNLOCODE信息
     * 
     * @param locode UNLOCODE主键
     * @return 结果
     */
    @Override
    public int deleteUnlocodeByLocode(String locode)
    {
        return unlocodeMapper.deleteUnlocodeByLocode(locode);
    }
}
