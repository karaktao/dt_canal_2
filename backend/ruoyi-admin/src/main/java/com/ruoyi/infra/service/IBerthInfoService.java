package com.ruoyi.infra.service;

import java.util.List;
import com.ruoyi.infra.domain.BerthInfo;

/**
 * berthInfoService接口
 * 
 * @author dt
 * @date 2025-06-26
 */
public interface IBerthInfoService 
{
    /**
     * 查询berthInfo
     * 
     * @param id berthInfo主键
     * @return berthInfo
     */
    public BerthInfo selectBerthInfoById(Long id);

    /**
     * 查询berthInfo列表
     * 
     * @param berthInfo berthInfo
     * @return berthInfo集合
     */
    public List<BerthInfo> selectBerthInfoList(BerthInfo berthInfo);

    /**
     * 新增berthInfo
     * 
     * @param berthInfo berthInfo
     * @return 结果
     */
    public int insertBerthInfo(BerthInfo berthInfo);

    /**
     * 修改berthInfo
     * 
     * @param berthInfo berthInfo
     * @return 结果
     */
    public int updateBerthInfo(BerthInfo berthInfo);

    /**
     * 批量删除berthInfo
     * 
     * @param ids 需要删除的berthInfo主键集合
     * @return 结果
     */
    public int deleteBerthInfoByIds(Long[] ids);

    /**
     * 删除berthInfo信息
     * 
     * @param id berthInfo主键
     * @return 结果
     */
    public int deleteBerthInfoById(Long id);
}
