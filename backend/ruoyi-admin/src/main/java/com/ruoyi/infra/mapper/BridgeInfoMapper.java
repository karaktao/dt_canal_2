package com.ruoyi.infra.mapper;

import java.util.List;
import com.ruoyi.infra.domain.BridgeInfo;

/**
 * bridgeinfoMapper接口
 * 
 * @author dt
 * @date 2025-06-26
 */
public interface BridgeInfoMapper 
{
    /**
     * 查询bridgeinfo
     * 
     * @param id bridgeinfo主键
     * @return bridgeinfo
     */
    public BridgeInfo selectBridgeInfoById(Long id);

    /**
     * 查询bridgeinfo列表
     * 
     * @param bridgeInfo bridgeinfo
     * @return bridgeinfo集合
     */
    public List<BridgeInfo> selectBridgeInfoList(BridgeInfo bridgeInfo);

    /**
     * 新增bridgeinfo
     * 
     * @param bridgeInfo bridgeinfo
     * @return 结果
     */
    public int insertBridgeInfo(BridgeInfo bridgeInfo);

    /**
     * 修改bridgeinfo
     * 
     * @param bridgeInfo bridgeinfo
     * @return 结果
     */
    public int updateBridgeInfo(BridgeInfo bridgeInfo);

    /**
     * 删除bridgeinfo
     * 
     * @param id bridgeinfo主键
     * @return 结果
     */
    public int deleteBridgeInfoById(Long id);

    /**
     * 批量删除bridgeinfo
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBridgeInfoByIds(Long[] ids);
}
