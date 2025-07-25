package com.ruoyi.infra.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infra.mapper.LockInfoMapper;
import com.ruoyi.infra.domain.LockInfo;
import com.ruoyi.infra.service.ILockInfoService;

/**
 * lockinfoService业务层处理
 * 
 * @author dt
 * @date 2025-06-26
 */
@Service
public class LockInfoServiceImpl implements ILockInfoService 
{
    @Autowired
    private LockInfoMapper lockInfoMapper;

    /**
     * 查询lockinfo
     * 
     * @param id lockinfo主键
     * @return lockinfo
     */
    @Override
    public LockInfo selectLockInfoById(Long id)
    {
        return lockInfoMapper.selectLockInfoById(id);
    }

    /**
     * 查询lockinfo列表
     * 
     * @param lockInfo lockinfo
     * @return lockinfo
     */
    @Override
    public List<LockInfo> selectLockInfoList(LockInfo lockInfo)
    {
        return lockInfoMapper.selectLockInfoList(lockInfo);
    }

    /**
     * 新增lockinfo
     * 
     * @param lockInfo lockinfo
     * @return 结果
     */
    @Override
    public int insertLockInfo(LockInfo lockInfo)
    {
        return lockInfoMapper.insertLockInfo(lockInfo);
    }

    /**
     * 修改lockinfo
     * 
     * @param lockInfo lockinfo
     * @return 结果
     */
    @Override
    public int updateLockInfo(LockInfo lockInfo)
    {
        return lockInfoMapper.updateLockInfo(lockInfo);
    }

    /**
     * 批量删除lockinfo
     * 
     * @param ids 需要删除的lockinfo主键
     * @return 结果
     */
    @Override
    public int deleteLockInfoByIds(Long[] ids)
    {
        return lockInfoMapper.deleteLockInfoByIds(ids);
    }

    /**
     * 删除lockinfo信息
     * 
     * @param id lockinfo主键
     * @return 结果
     */
    @Override
    public int deleteLockInfoById(Long id)
    {
        return lockInfoMapper.deleteLockInfoById(id);
    }
}
