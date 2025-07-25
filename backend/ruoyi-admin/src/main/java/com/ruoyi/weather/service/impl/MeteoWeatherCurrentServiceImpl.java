package com.ruoyi.weather.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weather.mapper.MeteoWeatherCurrentMapper;
import com.ruoyi.weather.domain.MeteoWeatherCurrent;
import com.ruoyi.weather.service.IMeteoWeatherCurrentService;

/**
 * weatherCurrentService业务层处理
 * 
 * @author dt
 * @date 2025-07-03
 */
@Service
public class MeteoWeatherCurrentServiceImpl implements IMeteoWeatherCurrentService 
{
    @Autowired
    private MeteoWeatherCurrentMapper meteoWeatherCurrentMapper;

    /**
     * 查询weatherCurrent
     * 
     * @param id weatherCurrent主键
     * @return weatherCurrent
     */
    @Override
    public MeteoWeatherCurrent selectMeteoWeatherCurrentById(Long id)
    {
        return meteoWeatherCurrentMapper.selectMeteoWeatherCurrentById(id);
    }

    /**
     * 查询weatherCurrent列表
     * 
     * @param meteoWeatherCurrent weatherCurrent
     * @return weatherCurrent
     */
    @Override
    public List<MeteoWeatherCurrent> selectMeteoWeatherCurrentList(MeteoWeatherCurrent meteoWeatherCurrent)
    {
        return meteoWeatherCurrentMapper.selectMeteoWeatherCurrentList(meteoWeatherCurrent);
    }

    /**
     * 新增weatherCurrent
     * 
     * @param meteoWeatherCurrent weatherCurrent
     * @return 结果
     */
    @Override
    public int insertMeteoWeatherCurrent(MeteoWeatherCurrent meteoWeatherCurrent)
    {
        return meteoWeatherCurrentMapper.insertMeteoWeatherCurrent(meteoWeatherCurrent);
    }

    /**
     * 修改weatherCurrent
     * 
     * @param meteoWeatherCurrent weatherCurrent
     * @return 结果
     */
    @Override
    public int updateMeteoWeatherCurrent(MeteoWeatherCurrent meteoWeatherCurrent)
    {
        return meteoWeatherCurrentMapper.updateMeteoWeatherCurrent(meteoWeatherCurrent);
    }

    /**
     * 批量删除weatherCurrent
     * 
     * @param ids 需要删除的weatherCurrent主键
     * @return 结果
     */
    @Override
    public int deleteMeteoWeatherCurrentByIds(Long[] ids)
    {
        return meteoWeatherCurrentMapper.deleteMeteoWeatherCurrentByIds(ids);
    }

    /**
     * 删除weatherCurrent信息
     * 
     * @param id weatherCurrent主键
     * @return 结果
     */
    @Override
    public int deleteMeteoWeatherCurrentById(Long id)
    {
        return meteoWeatherCurrentMapper.deleteMeteoWeatherCurrentById(id);
    }
}
