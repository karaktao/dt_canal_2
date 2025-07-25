package com.ruoyi.weather.service.impl;

import java.util.List;
import com.ruoyi.weather.domain.MeteoWeatherUnit;

/**
 * weatherUnitService接口
 * 
 * @author dt
 * @date 2025-07-03
 */
public interface IMeteoWeatherUnitService 
{
    /**
     * 查询weatherUnit
     * 
     * @param id weatherUnit主键
     * @return weatherUnit
     */
    public MeteoWeatherUnit selectMeteoWeatherUnitById(Long id);

    /**
     * 查询weatherUnit列表
     * 
     * @param meteoWeatherUnit weatherUnit
     * @return weatherUnit集合
     */
    public List<MeteoWeatherUnit> selectMeteoWeatherUnitList(MeteoWeatherUnit meteoWeatherUnit);

    /**
     * 新增weatherUnit
     * 
     * @param meteoWeatherUnit weatherUnit
     * @return 结果
     */
    public int insertMeteoWeatherUnit(MeteoWeatherUnit meteoWeatherUnit);

    /**
     * 修改weatherUnit
     * 
     * @param meteoWeatherUnit weatherUnit
     * @return 结果
     */
    public int updateMeteoWeatherUnit(MeteoWeatherUnit meteoWeatherUnit);

    /**
     * 批量删除weatherUnit
     * 
     * @param ids 需要删除的weatherUnit主键集合
     * @return 结果
     */
    public int deleteMeteoWeatherUnitByIds(Long[] ids);

    /**
     * 删除weatherUnit信息
     * 
     * @param id weatherUnit主键
     * @return 结果
     */
    public int deleteMeteoWeatherUnitById(Long id);
}
