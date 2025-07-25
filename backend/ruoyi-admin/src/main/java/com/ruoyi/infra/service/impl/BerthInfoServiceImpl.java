package com.ruoyi.infra.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infra.mapper.BerthInfoMapper;
import com.ruoyi.infra.domain.BerthInfo;
import com.ruoyi.infra.service.IBerthInfoService;

/**
 * berthInfoService业务层处理
 * 
 * @author dt
 * @date 2025-06-26
 */
@Service
public class BerthInfoServiceImpl implements IBerthInfoService 
{
    @Autowired
    private BerthInfoMapper berthInfoMapper;

    /**
     * 查询berthInfo
     * 
     * @param id berthInfo主键
     * @return berthInfo
     */
    @Override
    public BerthInfo selectBerthInfoById(Long id)
    {
        return berthInfoMapper.selectBerthInfoById(id);
    }

    /**
     * 查询berthInfo列表
     * 
     * @param berthInfo berthInfo
     * @return berthInfo
     */
    @Override
    public List<BerthInfo> selectBerthInfoList(BerthInfo berthInfo)
    {
        return berthInfoMapper.selectBerthInfoList(berthInfo);
    }

    /**
     * 新增berthInfo
     * 
     * @param berthInfo berthInfo
     * @return 结果
     */
    @Override
    public int insertBerthInfo(BerthInfo berthInfo)
    {
        return berthInfoMapper.insertBerthInfo(berthInfo);
    }

    /**
     * 修改berthInfo
     * 
     * @param berthInfo berthInfo
     * @return 结果
     */
    @Override
    public int updateBerthInfo(BerthInfo berthInfo)
    {
        return berthInfoMapper.updateBerthInfo(berthInfo);
    }

    /**
     * 批量删除berthInfo
     * 
     * @param ids 需要删除的berthInfo主键
     * @return 结果
     */
    @Override
    public int deleteBerthInfoByIds(Long[] ids)
    {
        return berthInfoMapper.deleteBerthInfoByIds(ids);
    }

    /**
     * 删除berthInfo信息
     * 
     * @param id berthInfo主键
     * @return 结果
     */
    @Override
    public int deleteBerthInfoById(Long id)
    {
        return berthInfoMapper.deleteBerthInfoById(id);
    }
}
