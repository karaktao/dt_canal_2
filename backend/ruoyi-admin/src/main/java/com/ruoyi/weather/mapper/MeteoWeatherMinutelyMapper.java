package com.ruoyi.weather.mapper;

import java.util.List;
import com.ruoyi.weather.domain.MeteoWeatherMinutely;

/**
 * weatherMinutelyMapper接口
 * 
 * @author dt
 * @date 2025-07-03
 */
public interface MeteoWeatherMinutelyMapper 
{
    /**
     * 查询weatherMinutely
     * 
     * @param id weatherMinutely主键
     * @return weatherMinutely
     */
    public MeteoWeatherMinutely selectMeteoWeatherMinutelyById(Long id);

    /**
     * 查询weatherMinutely列表
     * 
     * @param meteoWeatherMinutely weatherMinutely
     * @return weatherMinutely集合
     */
    public List<MeteoWeatherMinutely> selectMeteoWeatherMinutelyList(MeteoWeatherMinutely meteoWeatherMinutely);

    /**
     * 新增weatherMinutely
     * 
     * @param meteoWeatherMinutely weatherMinutely
     * @return 结果
     */
    public int insertMeteoWeatherMinutely(MeteoWeatherMinutely meteoWeatherMinutely);

    /**
     * 修改weatherMinutely
     * 
     * @param meteoWeatherMinutely weatherMinutely
     * @return 结果
     */
    public int updateMeteoWeatherMinutely(MeteoWeatherMinutely meteoWeatherMinutely);

    /**
     * 删除weatherMinutely
     * 
     * @param id weatherMinutely主键
     * @return 结果
     */
    public int deleteMeteoWeatherMinutelyById(Long id);

    /**
     * 批量删除weatherMinutely
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMeteoWeatherMinutelyByIds(Long[] ids);
}
