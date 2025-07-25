package com.ruoyi.infra.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infra.mapper.BridgeInfoMapper;
import com.ruoyi.infra.domain.BridgeInfo;
import com.ruoyi.infra.service.IBridgeInfoService;

/**
 * bridgeinfoService业务层处理
 * 
 * @author dt
 * @date 2025-06-26
 */
@Service
public class BridgeInfoServiceImpl implements IBridgeInfoService 
{
    @Autowired
    private BridgeInfoMapper bridgeInfoMapper;

    /**
     * 查询bridgeinfo
     * 
     * @param id bridgeinfo主键
     * @return bridgeinfo
     */
    @Override
    public BridgeInfo selectBridgeInfoById(Long id)
    {
        return bridgeInfoMapper.selectBridgeInfoById(id);
    }

    /**
     * 查询bridgeinfo列表
     * 
     * @param bridgeInfo bridgeinfo
     * @return bridgeinfo
     */
    @Override
    public List<BridgeInfo> selectBridgeInfoList(BridgeInfo bridgeInfo)
    {
        return bridgeInfoMapper.selectBridgeInfoList(bridgeInfo);
    }

    /**
     * 新增bridgeinfo
     * 
     * @param bridgeInfo bridgeinfo
     * @return 结果
     */
    @Override
    public int insertBridgeInfo(BridgeInfo bridgeInfo)
    {
        return bridgeInfoMapper.insertBridgeInfo(bridgeInfo);
    }

    /**
     * 修改bridgeinfo
     * 
     * @param bridgeInfo bridgeinfo
     * @return 结果
     */
    @Override
    public int updateBridgeInfo(BridgeInfo bridgeInfo)
    {
        return bridgeInfoMapper.updateBridgeInfo(bridgeInfo);
    }

    /**
     * 批量删除bridgeinfo
     * 
     * @param ids 需要删除的bridgeinfo主键
     * @return 结果
     */
    @Override
    public int deleteBridgeInfoByIds(Long[] ids)
    {
        return bridgeInfoMapper.deleteBridgeInfoByIds(ids);
    }

    /**
     * 删除bridgeinfo信息
     * 
     * @param id bridgeinfo主键
     * @return 结果
     */
    @Override
    public int deleteBridgeInfoById(Long id)
    {
        return bridgeInfoMapper.deleteBridgeInfoById(id);
    }
}
