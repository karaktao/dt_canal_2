package com.ruoyi.weather.service.impl;

import java.util.List;

import com.ruoyi.weather.mapper.MeteoWeatherCodeDictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weather.domain.MeteoWeatherUnit;

/**
 * weatherUnitService业务层处理
 * 
 * @author dt
 * @date 2025-07-03
 */
@Service
public class MeteoWeatherUnitServiceImpl implements IMeteoWeatherUnitService 
{
    @Autowired
    private MeteoWeatherCodeDictMapper.MeteoWeatherUnitMapper meteoWeatherUnitMapper;

    /**
     * 查询weatherUnit
     * 
     * @param id weatherUnit主键
     * @return weatherUnit
     */
    @Override
    public MeteoWeatherUnit selectMeteoWeatherUnitById(Long id)
    {
        return meteoWeatherUnitMapper.selectMeteoWeatherUnitById(id);
    }

    /**
     * 查询weatherUnit列表
     * 
     * @param meteoWeatherUnit weatherUnit
     * @return weatherUnit
     */
    @Override
    public List<MeteoWeatherUnit> selectMeteoWeatherUnitList(MeteoWeatherUnit meteoWeatherUnit)
    {
        return meteoWeatherUnitMapper.selectMeteoWeatherUnitList(meteoWeatherUnit);
    }

    /**
     * 新增weatherUnit
     * 
     * @param meteoWeatherUnit weatherUnit
     * @return 结果
     */
    @Override
    public int insertMeteoWeatherUnit(MeteoWeatherUnit meteoWeatherUnit)
    {
        return meteoWeatherUnitMapper.insertMeteoWeatherUnit(meteoWeatherUnit);
    }

    /**
     * 修改weatherUnit
     * 
     * @param meteoWeatherUnit weatherUnit
     * @return 结果
     */
    @Override
    public int updateMeteoWeatherUnit(MeteoWeatherUnit meteoWeatherUnit)
    {
        return meteoWeatherUnitMapper.updateMeteoWeatherUnit(meteoWeatherUnit);
    }

    /**
     * 批量删除weatherUnit
     * 
     * @param ids 需要删除的weatherUnit主键
     * @return 结果
     */
    @Override
    public int deleteMeteoWeatherUnitByIds(Long[] ids)
    {
        return meteoWeatherUnitMapper.deleteMeteoWeatherUnitByIds(ids);
    }

    /**
     * 删除weatherUnit信息
     * 
     * @param id weatherUnit主键
     * @return 结果
     */
    @Override
    public int deleteMeteoWeatherUnitById(Long id)
    {
        return meteoWeatherUnitMapper.deleteMeteoWeatherUnitById(id);
    }
}
