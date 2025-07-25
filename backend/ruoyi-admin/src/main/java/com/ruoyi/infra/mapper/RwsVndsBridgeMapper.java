package com.ruoyi.infra.mapper;

import java.util.List;
import com.ruoyi.infra.domain.RwsVndsBridge;

/**
 * VNDSbridgeMapper接口
 * 
 * @author dt
 * @date 2025-07-25
 */
public interface RwsVndsBridgeMapper 
{
    /**
     * 查询VNDSbridge
     * 
     * @param Id VNDSbridge主键
     * @return VNDSbridge
     */
    public RwsVndsBridge selectRwsVndsBridgeById(Long Id);

    /**
     * 查询VNDSbridge列表
     * 
     * @param rwsVndsBridge VNDSbridge
     * @return VNDSbridge集合
     */
    public List<RwsVndsBridge> selectRwsVndsBridgeList(RwsVndsBridge rwsVndsBridge);

    /**
     * 新增VNDSbridge
     * 
     * @param rwsVndsBridge VNDSbridge
     * @return 结果
     */
    public int insertRwsVndsBridge(RwsVndsBridge rwsVndsBridge);

    /**
     * 修改VNDSbridge
     * 
     * @param rwsVndsBridge VNDSbridge
     * @return 结果
     */
    public int updateRwsVndsBridge(RwsVndsBridge rwsVndsBridge);

    /**
     * 删除VNDSbridge
     * 
     * @param Id VNDSbridge主键
     * @return 结果
     */
    public int deleteRwsVndsBridgeById(Long Id);

    /**
     * 批量删除VNDSbridge
     * 
     * @param Ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRwsVndsBridgeByIds(Long[] Ids);
}
