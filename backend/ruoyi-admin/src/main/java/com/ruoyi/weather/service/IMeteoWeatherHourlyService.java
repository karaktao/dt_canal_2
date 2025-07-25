package com.ruoyi.weather.service;

import java.util.List;
import com.ruoyi.weather.domain.MeteoWeatherHourly;

/**
 * weatherHourlyService接口
 * 
 * @author dt
 * @date 2025-07-03
 */
public interface IMeteoWeatherHourlyService 
{
    /**
     * 查询weatherHourly
     * 
     * @param id weatherHourly主键
     * @return weatherHourly
     */
    public MeteoWeatherHourly selectMeteoWeatherHourlyById(Long id);

    /**
     * 查询weatherHourly列表
     * 
     * @param meteoWeatherHourly weatherHourly
     * @return weatherHourly集合
     */
    public List<MeteoWeatherHourly> selectMeteoWeatherHourlyList(MeteoWeatherHourly meteoWeatherHourly);

    /**
     * 新增weatherHourly
     * 
     * @param meteoWeatherHourly weatherHourly
     * @return 结果
     */
    public int insertMeteoWeatherHourly(MeteoWeatherHourly meteoWeatherHourly);

    /**
     * 修改weatherHourly
     * 
     * @param meteoWeatherHourly weatherHourly
     * @return 结果
     */
    public int updateMeteoWeatherHourly(MeteoWeatherHourly meteoWeatherHourly);

    /**
     * 批量删除weatherHourly
     * 
     * @param ids 需要删除的weatherHourly主键集合
     * @return 结果
     */
    public int deleteMeteoWeatherHourlyByIds(Long[] ids);

    /**
     * 删除weatherHourly信息
     * 
     * @param id weatherHourly主键
     * @return 结果
     */
    public int deleteMeteoWeatherHourlyById(Long id);
}
