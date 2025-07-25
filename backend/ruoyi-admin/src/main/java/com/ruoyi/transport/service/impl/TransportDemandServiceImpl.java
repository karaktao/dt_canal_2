package com.ruoyi.transport.service.impl;

import java.util.List;
import java.util.Map;

import com.ruoyi.transport.domain.TransportDemandWithCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.transport.mapper.TransportDemandMapper;
import com.ruoyi.transport.domain.TransportDemand;
import com.ruoyi.transport.service.ITransportDemandService;


/**
 * 物流发布Service业务层处理
 * 
 * @author dt
 * @date 2025-06-25
 */
@Service
public class TransportDemandServiceImpl implements ITransportDemandService 
{
    @Autowired
    private TransportDemandMapper transportDemandMapper;

    /**
     * 查询物流发布
     * 
     * @param id 物流发布主键
     * @return 物流发布
     */
    @Override
    public TransportDemand selectTransportDemandById(Long id)
    {
        return transportDemandMapper.selectTransportDemandById(id);
    }


    /**
     * 查询物流发布列表
     * 
     * @param transportDemand 物流发布
     * @return 物流发布
     */
    @Override
    public List<TransportDemand> selectTransportDemandList(TransportDemand transportDemand)
    {
        return transportDemandMapper.selectTransportDemandList(transportDemand);
    }

    /**
     * 新增物流发布
     * 
     * @param transportDemand 物流发布
     * @return 结果
     */
    @Override
    public int insertTransportDemand(TransportDemand transportDemand)
    {
        return transportDemandMapper.insertTransportDemand(transportDemand);
    }

    /**
     * 修改物流发布
     * 
     * @param transportDemand 物流发布
     * @return 结果
     */
    @Override
    public int updateTransportDemand(TransportDemand transportDemand)
    {
        return transportDemandMapper.updateTransportDemand(transportDemand);
    }

    /**
     * 批量删除物流发布
     * 
     * @param ids 需要删除的物流发布主键
     * @return 结果
     */
    @Override
    public int deleteTransportDemandByIds(Long[] ids)
    {
        return transportDemandMapper.deleteTransportDemandByIds(ids);
    }

    /**
     * 删除物流发布信息
     * 
     * @param id 物流发布主键
     * @return 结果
     */
    @Override
    public int deleteTransportDemandById(Long id)
    {
        return transportDemandMapper.deleteTransportDemandById(id);
    }
}
