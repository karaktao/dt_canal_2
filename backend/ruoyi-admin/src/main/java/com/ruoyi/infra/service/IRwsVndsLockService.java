package com.ruoyi.infra.service;

import java.util.List;
import com.ruoyi.infra.domain.RwsVndsLock;

/**
 * VNDSlockService接口
 * 
 * @author dt
 * @date 2025-07-25
 */
public interface IRwsVndsLockService 
{
    /**
     * 查询VNDSlock
     * 
     * @param Id VNDSlock主键
     * @return VNDSlock
     */
    public RwsVndsLock selectRwsVndsLockById(Long Id);

    /**
     * 查询VNDSlock列表
     * 
     * @param rwsVndsLock VNDSlock
     * @return VNDSlock集合
     */
    public List<RwsVndsLock> selectRwsVndsLockList(RwsVndsLock rwsVndsLock);

    /**
     * 新增VNDSlock
     * 
     * @param rwsVndsLock VNDSlock
     * @return 结果
     */
    public int insertRwsVndsLock(RwsVndsLock rwsVndsLock);

    /**
     * 修改VNDSlock
     * 
     * @param rwsVndsLock VNDSlock
     * @return 结果
     */
    public int updateRwsVndsLock(RwsVndsLock rwsVndsLock);

    /**
     * 批量删除VNDSlock
     * 
     * @param Ids 需要删除的VNDSlock主键集合
     * @return 结果
     */
    public int deleteRwsVndsLockByIds(Long[] Ids);

    /**
     * 删除VNDSlock信息
     * 
     * @param Id VNDSlock主键
     * @return 结果
     */
    public int deleteRwsVndsLockById(Long Id);
}
