package com.ruoyi.infra.mapper;

import java.util.List;
import com.ruoyi.infra.domain.LockInfo;

/**
 * lockinfoMapper接口
 * 
 * @author dt
 * @date 2025-06-26
 */
public interface LockInfoMapper 
{
    /**
     * 查询lockinfo
     * 
     * @param id lockinfo主键
     * @return lockinfo
     */
    public LockInfo selectLockInfoById(Long id);

    /**
     * 查询lockinfo列表
     * 
     * @param lockInfo lockinfo
     * @return lockinfo集合
     */
    public List<LockInfo> selectLockInfoList(LockInfo lockInfo);

    /**
     * 新增lockinfo
     * 
     * @param lockInfo lockinfo
     * @return 结果
     */
    public int insertLockInfo(LockInfo lockInfo);

    /**
     * 修改lockinfo
     * 
     * @param lockInfo lockinfo
     * @return 结果
     */
    public int updateLockInfo(LockInfo lockInfo);

    /**
     * 删除lockinfo
     * 
     * @param id lockinfo主键
     * @return 结果
     */
    public int deleteLockInfoById(Long id);

    /**
     * 批量删除lockinfo
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLockInfoByIds(Long[] ids);
}
