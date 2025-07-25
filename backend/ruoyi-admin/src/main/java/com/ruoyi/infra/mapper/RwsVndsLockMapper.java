package com.ruoyi.infra.mapper;

import java.util.List;
import com.ruoyi.infra.domain.RwsVndsLock;

/**
 * VNDSlockMapper接口
 * 
 * @author dt
 * @date 2025-07-25
 */
public interface RwsVndsLockMapper 
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
     * 删除VNDSlock
     * 
     * @param Id VNDSlock主键
     * @return 结果
     */
    public int deleteRwsVndsLockById(Long Id);

    /**
     * 批量删除VNDSlock
     * 
     * @param Ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRwsVndsLockByIds(Long[] Ids);
}
