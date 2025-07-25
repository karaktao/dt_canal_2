package com.ruoyi.infra.mapper;

import java.util.List;
import com.ruoyi.infra.domain.Unlocode;

/**
 * UNLOCODEMapper接口
 * 
 * @author dt
 * @date 2025-06-28
 */
public interface UnlocodeMapper 
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
     * 删除UNLOCODE
     * 
     * @param locode UNLOCODE主键
     * @return 结果
     */
    public int deleteUnlocodeByLocode(String locode);

    /**
     * 批量删除UNLOCODE
     * 
     * @param locodes 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUnlocodeByLocodes(String[] locodes);
}
