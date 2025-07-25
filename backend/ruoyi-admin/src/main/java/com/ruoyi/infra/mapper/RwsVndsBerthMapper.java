package com.ruoyi.infra.mapper;

import java.util.List;
import com.ruoyi.infra.domain.RwsVndsBerth;

/**
 * VNDS-berthMapper接口
 * 
 * @author dt
 * @date 2025-07-25
 */
public interface RwsVndsBerthMapper 
{
    /**
     * 查询VNDS-berth
     * 
     * @param Id VNDS-berth主键
     * @return VNDS-berth
     */
    public RwsVndsBerth selectRwsVndsBerthById(Long Id);

    /**
     * 查询VNDS-berth列表
     * 
     * @param rwsVndsBerth VNDS-berth
     * @return VNDS-berth集合
     */
    public List<RwsVndsBerth> selectRwsVndsBerthList(RwsVndsBerth rwsVndsBerth);

    /**
     * 新增VNDS-berth
     * 
     * @param rwsVndsBerth VNDS-berth
     * @return 结果
     */
    public int insertRwsVndsBerth(RwsVndsBerth rwsVndsBerth);

    /**
     * 修改VNDS-berth
     * 
     * @param rwsVndsBerth VNDS-berth
     * @return 结果
     */
    public int updateRwsVndsBerth(RwsVndsBerth rwsVndsBerth);

    /**
     * 删除VNDS-berth
     * 
     * @param Id VNDS-berth主键
     * @return 结果
     */
    public int deleteRwsVndsBerthById(Long Id);

    /**
     * 批量删除VNDS-berth
     * 
     * @param Ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRwsVndsBerthByIds(Long[] Ids);
}
