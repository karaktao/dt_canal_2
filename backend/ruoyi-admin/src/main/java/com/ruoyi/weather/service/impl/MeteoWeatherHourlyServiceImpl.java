package com.ruoyi.weather.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weather.mapper.MeteoWeatherHourlyMapper;
import com.ruoyi.weather.domain.MeteoWeatherHourly;
import com.ruoyi.weather.service.IMeteoWeatherHourlyService;

/**
 * weatherHourlyService业务层处理
 * 
 * @author dt
 * @date 2025-07-03
 */
@Service
public class MeteoWeatherHourlyServiceImpl implements IMeteoWeatherHourlyService 
{
    @Autowired
    private MeteoWeatherHourlyMapper meteoWeatherHourlyMapper;

    /**
     * 查询weatherHourly
     * 
     * @param id weatherHourly主键
     * @return weatherHourly
     */
    @Override
    public MeteoWeatherHourly selectMeteoWeatherHourlyById(Long id)
    {
        return meteoWeatherHourlyMapper.selectMeteoWeatherHourlyById(id);
    }

    /**
     * 查询weatherHourly列表
     * 
     * @param meteoWeatherHourly weatherHourly
     * @return weatherHourly
     */
    @Override
    public List<MeteoWeatherHourly> selectMeteoWeatherHourlyList(MeteoWeatherHourly meteoWeatherHourly)
    {
        return meteoWeatherHourlyMapper.selectMeteoWeatherHourlyList(meteoWeatherHourly);
    }

    /**
     * 新增weatherHourly
     * 
     * @param meteoWeatherHourly weatherHourly
     * @return 结果
     */
    @Override
    public int insertMeteoWeatherHourly(MeteoWeatherHourly meteoWeatherHourly)
    {
        return meteoWeatherHourlyMapper.insertMeteoWeatherHourly(meteoWeatherHourly);
    }

    /**
     * 修改weatherHourly
     * 
     * @param meteoWeatherHourly weatherHourly
     * @return 结果
     */
    @Override
    public int updateMeteoWeatherHourly(MeteoWeatherHourly meteoWeatherHourly)
    {
        return meteoWeatherHourlyMapper.updateMeteoWeatherHourly(meteoWeatherHourly);
    }

    /**
     * 批量删除weatherHourly
     * 
     * @param ids 需要删除的weatherHourly主键
     * @return 结果
     */
    @Override
    public int deleteMeteoWeatherHourlyByIds(Long[] ids)
    {
        return meteoWeatherHourlyMapper.deleteMeteoWeatherHourlyByIds(ids);
    }

    /**
     * 删除weatherHourly信息
     * 
     * @param id weatherHourly主键
     * @return 结果
     */
    @Override
    public int deleteMeteoWeatherHourlyById(Long id)
    {
        return meteoWeatherHourlyMapper.deleteMeteoWeatherHourlyById(id);
    }
}
