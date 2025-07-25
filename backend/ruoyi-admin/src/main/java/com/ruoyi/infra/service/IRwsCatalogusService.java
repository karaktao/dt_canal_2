package com.ruoyi.infra.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.infra.domain.RwsCatalogus;

/**
 * rwsCatalogusService接口
 *
 * @author dt
 * @date 2025-07-07
 */
public interface IRwsCatalogusService  extends IService<RwsCatalogus>
{
    /**
     * 查询rwsCatalogus
     *
     * @param id rwsCatalogus主键
     * @return rwsCatalogus
     */
    public RwsCatalogus selectRwsCatalogusById(Long id);

    /**
     * 查询rwsCatalogus列表
     *
     * @param rwsCatalogus rwsCatalogus
     * @return rwsCatalogus集合
     */
    public List<RwsCatalogus> selectRwsCatalogusList(RwsCatalogus rwsCatalogus);




    /**
     * 新增rwsCatalogus
     *
     * @param rwsCatalogus rwsCatalogus
     * @return 结果
     */
    public int insertRwsCatalogus(RwsCatalogus rwsCatalogus);

    /**
     * 修改rwsCatalogus
     *
     * @param rwsCatalogus rwsCatalogus
     * @return 结果
     */
    public int updateRwsCatalogus(RwsCatalogus rwsCatalogus);

    /**
     * 批量删除rwsCatalogus
     *
     * @param ids 需要删除的rwsCatalogus主键集合
     * @return 结果
     */
    public int deleteRwsCatalogusByIds(Long[] ids);

    /**
     * 删除rwsCatalogus信息
     *
     * @param id rwsCatalogus主键
     * @return 结果
     */
    public int deleteRwsCatalogusById(Long id);
}
