package com.ruoyi.infra.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infra.mapper.RwsVndsBerthMapper;
import com.ruoyi.infra.domain.RwsVndsBerth;
import com.ruoyi.infra.service.IRwsVndsBerthService;

/**
 * VNDS-berthService业务层处理
 * 
 * @author dt
 * @date 2025-07-25
 */
@Service
public class RwsVndsBerthServiceImpl implements IRwsVndsBerthService 
{
    @Autowired
    private RwsVndsBerthMapper rwsVndsBerthMapper;

    /**
     * 查询VNDS-berth
     * 
     * @param Id VNDS-berth主键
     * @return VNDS-berth
     */
    @Override
    public RwsVndsBerth selectRwsVndsBerthById(Long Id)
    {
        return rwsVndsBerthMapper.selectRwsVndsBerthById(Id);
    }

    /**
     * 查询VNDS-berth列表
     * 
     * @param rwsVndsBerth VNDS-berth
     * @return VNDS-berth
     */
    @Override
    public List<RwsVndsBerth> selectRwsVndsBerthList(RwsVndsBerth rwsVndsBerth)
    {
        return rwsVndsBerthMapper.selectRwsVndsBerthList(rwsVndsBerth);
    }

    /**
     * 新增VNDS-berth
     * 
     * @param rwsVndsBerth VNDS-berth
     * @return 结果
     */
    @Override
    public int insertRwsVndsBerth(RwsVndsBerth rwsVndsBerth)
    {
        return rwsVndsBerthMapper.insertRwsVndsBerth(rwsVndsBerth);
    }

    /**
     * 修改VNDS-berth
     * 
     * @param rwsVndsBerth VNDS-berth
     * @return 结果
     */
    @Override
    public int updateRwsVndsBerth(RwsVndsBerth rwsVndsBerth)
    {
        return rwsVndsBerthMapper.updateRwsVndsBerth(rwsVndsBerth);
    }

    /**
     * 批量删除VNDS-berth
     * 
     * @param Ids 需要删除的VNDS-berth主键
     * @return 结果
     */
    @Override
    public int deleteRwsVndsBerthByIds(Long[] Ids)
    {
        return rwsVndsBerthMapper.deleteRwsVndsBerthByIds(Ids);
    }

    /**
     * 删除VNDS-berth信息
     * 
     * @param Id VNDS-berth主键
     * @return 结果
     */
    @Override
    public int deleteRwsVndsBerthById(Long Id)
    {
        return rwsVndsBerthMapper.deleteRwsVndsBerthById(Id);
    }
}
