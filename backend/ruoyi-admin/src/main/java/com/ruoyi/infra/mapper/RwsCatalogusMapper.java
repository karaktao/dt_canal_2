package com.ruoyi.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.infra.domain.RwsCatalogus;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * rwsCatalogusMapper接口
 *
 * @author dt
 * @date 2025-07-07
 */
@Mapper
public interface RwsCatalogusMapper  extends BaseMapper<RwsCatalogus>
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
     * 删除rwsCatalogus
     *
     * @param id rwsCatalogus主键
     * @return 结果
     */
    public int deleteRwsCatalogusById(Long id);

    /**
     * 批量删除rwsCatalogus
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteRwsCatalogusByIds(Long[] ids);
}
