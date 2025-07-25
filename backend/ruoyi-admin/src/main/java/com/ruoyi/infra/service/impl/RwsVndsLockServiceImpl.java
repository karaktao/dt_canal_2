package com.ruoyi.infra.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infra.mapper.RwsVndsLockMapper;
import com.ruoyi.infra.domain.RwsVndsLock;
import com.ruoyi.infra.service.IRwsVndsLockService;

/**
 * VNDSlockService业务层处理
 * 
 * @author dt
 * @date 2025-07-25
 */
@Service
public class RwsVndsLockServiceImpl implements IRwsVndsLockService 
{
    @Autowired
    private RwsVndsLockMapper rwsVndsLockMapper;

    /**
     * 查询VNDSlock
     * 
     * @param Id VNDSlock主键
     * @return VNDSlock
     */
    @Override
    public RwsVndsLock selectRwsVndsLockById(Long Id)
    {
        return rwsVndsLockMapper.selectRwsVndsLockById(Id);
    }

    /**
     * 查询VNDSlock列表
     * 
     * @param rwsVndsLock VNDSlock
     * @return VNDSlock
     */
    @Override
    public List<RwsVndsLock> selectRwsVndsLockList(RwsVndsLock rwsVndsLock)
    {
        return rwsVndsLockMapper.selectRwsVndsLockList(rwsVndsLock);
    }

    /**
     * 新增VNDSlock
     * 
     * @param rwsVndsLock VNDSlock
     * @return 结果
     */
    @Override
    public int insertRwsVndsLock(RwsVndsLock rwsVndsLock)
    {
        return rwsVndsLockMapper.insertRwsVndsLock(rwsVndsLock);
    }

    /**
     * 修改VNDSlock
     * 
     * @param rwsVndsLock VNDSlock
     * @return 结果
     */
    @Override
    public int updateRwsVndsLock(RwsVndsLock rwsVndsLock)
    {
        return rwsVndsLockMapper.updateRwsVndsLock(rwsVndsLock);
    }

    /**
     * 批量删除VNDSlock
     * 
     * @param Ids 需要删除的VNDSlock主键
     * @return 结果
     */
    @Override
    public int deleteRwsVndsLockByIds(Long[] Ids)
    {
        return rwsVndsLockMapper.deleteRwsVndsLockByIds(Ids);
    }

    /**
     * 删除VNDSlock信息
     * 
     * @param Id VNDSlock主键
     * @return 结果
     */
    @Override
    public int deleteRwsVndsLockById(Long Id)
    {
        return rwsVndsLockMapper.deleteRwsVndsLockById(Id);
    }
}
