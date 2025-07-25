package com.ruoyi.transport.mapper;

import java.util.List;
import com.ruoyi.transport.domain.TransportDemand;
import com.ruoyi.transport.domain.TransportDemandWithCity;
import java.util.Map;


/**
 * 物流发布Mapper接口
 * 
 * @author dt
 * @date 2025-06-25
 */
public interface TransportDemandMapper 
{
    /**
     * 查询物流发布
     * 
     * @param id 物流发布主键
     * @return 物流发布
     */
    public TransportDemand selectTransportDemandById(Long id);

    /**
     * 查询物流发布列表
     * 
     * @param transportDemand 物流发布
     * @return 物流发布集合
     */
    public List<TransportDemand> selectTransportDemandList(TransportDemand transportDemand);

//    /**
//     * 查询已发布的 vessel_to_cargo，并带有城市信息
//     *
//     * @return 带城市名的物流发布集合
//     */
//    public List<TransportDemandWithCity> selectPublishedVesselToCargoWithCities(Map<String, Object> params);
//

    /**
     * 新增物流发布
     * 
     * @param transportDemand 物流发布
     * @return 结果
     */
    public int insertTransportDemand(TransportDemand transportDemand);

    /**
     * 修改物流发布
     * 
     * @param transportDemand 物流发布
     * @return 结果
     */
    public int updateTransportDemand(TransportDemand transportDemand);

    /**
     * 删除物流发布
     * 
     * @param id 物流发布主键
     * @return 结果
     */
    public int deleteTransportDemandById(Long id);

    /**
     * 批量删除物流发布
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTransportDemandByIds(Long[] ids);


}
