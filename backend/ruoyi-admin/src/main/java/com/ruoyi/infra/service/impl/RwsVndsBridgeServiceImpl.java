package com.ruoyi.infra.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infra.mapper.RwsVndsBridgeMapper;
import com.ruoyi.infra.domain.RwsVndsBridge;
import com.ruoyi.infra.service.IRwsVndsBridgeService;

/**
 * VNDSbridgeService业务层处理
 * 
 * @author dt
 * @date 2025-07-25
 */
@Service
public class RwsVndsBridgeServiceImpl implements IRwsVndsBridgeService 
{
    @Autowired
    private RwsVndsBridgeMapper rwsVndsBridgeMapper;

    /**
     * 查询VNDSbridge
     * 
     * @param Id VNDSbridge主键
     * @return VNDSbridge
     */
    @Override
    public RwsVndsBridge selectRwsVndsBridgeById(Long Id)
    {
        return rwsVndsBridgeMapper.selectRwsVndsBridgeById(Id);
    }

    /**
     * 查询VNDSbridge列表
     * 
     * @param rwsVndsBridge VNDSbridge
     * @return VNDSbridge
     */
    @Override
    public List<RwsVndsBridge> selectRwsVndsBridgeList(RwsVndsBridge rwsVndsBridge)
    {
        return rwsVndsBridgeMapper.selectRwsVndsBridgeList(rwsVndsBridge);
    }

    /**
     * 新增VNDSbridge
     * 
     * @param rwsVndsBridge VNDSbridge
     * @return 结果
     */
    @Override
    public int insertRwsVndsBridge(RwsVndsBridge rwsVndsBridge)
    {
        return rwsVndsBridgeMapper.insertRwsVndsBridge(rwsVndsBridge);
    }

    /**
     * 修改VNDSbridge
     * 
     * @param rwsVndsBridge VNDSbridge
     * @return 结果
     */
    @Override
    public int updateRwsVndsBridge(RwsVndsBridge rwsVndsBridge)
    {
        return rwsVndsBridgeMapper.updateRwsVndsBridge(rwsVndsBridge);
    }

    /**
     * 批量删除VNDSbridge
     * 
     * @param Ids 需要删除的VNDSbridge主键
     * @return 结果
     */
    @Override
    public int deleteRwsVndsBridgeByIds(Long[] Ids)
    {
        return rwsVndsBridgeMapper.deleteRwsVndsBridgeByIds(Ids);
    }

    /**
     * 删除VNDSbridge信息
     * 
     * @param Id VNDSbridge主键
     * @return 结果
     */
    @Override
    public int deleteRwsVndsBridgeById(Long Id)
    {
        return rwsVndsBridgeMapper.deleteRwsVndsBridgeById(Id);
    }
}
