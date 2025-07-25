package com.ruoyi.infra.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infra.mapper.RwsCatalogusMapper;
import com.ruoyi.infra.domain.RwsCatalogus;
import com.ruoyi.infra.service.IRwsCatalogusService;

/**
 * rwsCatalogusService业务层处理
 *
 * @author dt
 * @date 2025-07-07
 */
@Service
public class RwsCatalogusServiceImpl extends ServiceImpl<RwsCatalogusMapper, RwsCatalogus> implements IRwsCatalogusService
{
    @Autowired
    private RwsCatalogusMapper rwsCatalogusMapper;

    /**
     * 查询rwsCatalogus
     *
     * @param id rwsCatalogus主键
     * @return rwsCatalogus
     */
    @Override
    public RwsCatalogus selectRwsCatalogusById(Long id)
    {
        return rwsCatalogusMapper.selectRwsCatalogusById(id);
    }

    /**
     * 查询rwsCatalogus列表
     *
     * @param rwsCatalogus rwsCatalogus
     * @return rwsCatalogus
     */
    @Override
    public List<RwsCatalogus> selectRwsCatalogusList(RwsCatalogus rwsCatalogus)
    {
        return rwsCatalogusMapper.selectRwsCatalogusList(rwsCatalogus);
    }





    /**
     * 新增rwsCatalogus
     *
     * @param rwsCatalogus rwsCatalogus
     * @return 结果
     */
    @Override
    public int insertRwsCatalogus(RwsCatalogus rwsCatalogus)
    {
        return rwsCatalogusMapper.insertRwsCatalogus(rwsCatalogus);
    }

    /**
     * 修改rwsCatalogus
     *
     * @param rwsCatalogus rwsCatalogus
     * @return 结果
     */
    @Override
    public int updateRwsCatalogus(RwsCatalogus rwsCatalogus)
    {
        return rwsCatalogusMapper.updateRwsCatalogus(rwsCatalogus);
    }

    /**
     * 批量删除rwsCatalogus
     *
     * @param ids 需要删除的rwsCatalogus主键
     * @return 结果
     */
    @Override
    public int deleteRwsCatalogusByIds(Long[] ids)
    {
        return rwsCatalogusMapper.deleteRwsCatalogusByIds(ids);
    }

    /**
     * 删除rwsCatalogus信息
     *
     * @param id rwsCatalogus主键
     * @return 结果
     */
    @Override
    public int deleteRwsCatalogusById(Long id)
    {
        return rwsCatalogusMapper.deleteRwsCatalogusById(id);
    }
}
