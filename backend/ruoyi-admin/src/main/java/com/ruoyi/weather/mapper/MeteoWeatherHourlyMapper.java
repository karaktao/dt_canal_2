package com.ruoyi.weather.mapper;

import java.util.List;
import com.ruoyi.weather.domain.MeteoWeatherHourly;

/**
 * weatherHourlyMapper接口
 * 
 * @author dt
 * @date 2025-07-03
 */
public interface MeteoWeatherHourlyMapper 
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
     * 删除weatherHourly
     * 
     * @param id weatherHourly主键
     * @return 结果
     */
    public int deleteMeteoWeatherHourlyById(Long id);

    /**
     * 批量删除weatherHourly
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMeteoWeatherHourlyByIds(Long[] ids);
}
