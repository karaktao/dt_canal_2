package com.ruoyi.weather.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weather.mapper.MeteoWeatherMinutelyMapper;
import com.ruoyi.weather.domain.MeteoWeatherMinutely;
import com.ruoyi.weather.service.IMeteoWeatherMinutelyService;

/**
 * weatherMinutelyService业务层处理
 * 
 * @author dt
 * @date 2025-07-03
 */
@Service
public class MeteoWeatherMinutelyServiceImpl implements IMeteoWeatherMinutelyService 
{
    @Autowired
    private MeteoWeatherMinutelyMapper meteoWeatherMinutelyMapper;

    /**
     * 查询weatherMinutely
     * 
     * @param id weatherMinutely主键
     * @return weatherMinutely
     */
    @Override
    public MeteoWeatherMinutely selectMeteoWeatherMinutelyById(Long id)
    {
        return meteoWeatherMinutelyMapper.selectMeteoWeatherMinutelyById(id);
    }

    /**
     * 查询weatherMinutely列表
     * 
     * @param meteoWeatherMinutely weatherMinutely
     * @return weatherMinutely
     */
    @Override
    public List<MeteoWeatherMinutely> selectMeteoWeatherMinutelyList(MeteoWeatherMinutely meteoWeatherMinutely)
    {
        return meteoWeatherMinutelyMapper.selectMeteoWeatherMinutelyList(meteoWeatherMinutely);
    }

    /**
     * 新增weatherMinutely
     * 
     * @param meteoWeatherMinutely weatherMinutely
     * @return 结果
     */
    @Override
    public int insertMeteoWeatherMinutely(MeteoWeatherMinutely meteoWeatherMinutely)
    {
        return meteoWeatherMinutelyMapper.insertMeteoWeatherMinutely(meteoWeatherMinutely);
    }

    /**
     * 修改weatherMinutely
     * 
     * @param meteoWeatherMinutely weatherMinutely
     * @return 结果
     */
    @Override
    public int updateMeteoWeatherMinutely(MeteoWeatherMinutely meteoWeatherMinutely)
    {
        return meteoWeatherMinutelyMapper.updateMeteoWeatherMinutely(meteoWeatherMinutely);
    }

    /**
     * 批量删除weatherMinutely
     * 
     * @param ids 需要删除的weatherMinutely主键
     * @return 结果
     */
    @Override
    public int deleteMeteoWeatherMinutelyByIds(Long[] ids)
    {
        return meteoWeatherMinutelyMapper.deleteMeteoWeatherMinutelyByIds(ids);
    }

    /**
     * 删除weatherMinutely信息
     * 
     * @param id weatherMinutely主键
     * @return 结果
     */
    @Override
    public int deleteMeteoWeatherMinutelyById(Long id)
    {
        return meteoWeatherMinutelyMapper.deleteMeteoWeatherMinutelyById(id);
    }
}
