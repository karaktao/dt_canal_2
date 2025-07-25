package com.ruoyi.infra.service;

import java.util.List;
import com.ruoyi.infra.domain.Unlocode;

/**
 * UNLOCODEService接口
 * 
 * @author dt
 * @date 2025-06-28
 */
public interface IUnlocodeService 
{
    /**
     * 查询UNLOCODE
     * 
     * @param locode UNLOCODE主键
     * @return UNLOCODE
     */
    public Unlocode selectUnlocodeByLocode(String locode);

    /**
     * 查询UNLOCODE列表
     * 
     * @param unlocode UNLOCODE
     * @return UNLOCODE集合
     */
    public List<Unlocode> selectUnlocodeList(Unlocode unlocode);

    /**
     * 新增UNLOCODE
     * 
     * @param unlocode UNLOCODE
     * @return 结果
     */
    public int insertUnlocode(Unlocode unlocode);

    /**
     * 修改UNLOCODE
     * 
     * @param unlocode UNLOCODE
     * @return 结果
     */
    public int updateUnlocode(Unlocode unlocode);

    /**
     * 批量删除UNLOCODE
     * 
     * @param locodes 需要删除的UNLOCODE主键集合
     * @return 结果
     */
    public int deleteUnlocodeByLocodes(String[] locodes);

    /**
     * 删除UNLOCODE信息
     * 
     * @param locode UNLOCODE主键
     * @return 结果
     */
    public int deleteUnlocodeByLocode(String locode);
}
