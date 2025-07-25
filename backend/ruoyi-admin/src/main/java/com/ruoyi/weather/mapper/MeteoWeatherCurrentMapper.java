package com.ruoyi.weather.mapper;

import java.util.List;
import com.ruoyi.weather.domain.MeteoWeatherCurrent;

/**
 * weatherCurrentMapper接口
 * 
 * @author dt
 * @date 2025-07-03
 */
public interface MeteoWeatherCurrentMapper 
{
    /**
     * 查询weatherCurrent
     * 
     * @param id weatherCurrent主键
     * @return weatherCurrent
     */
    public MeteoWeatherCurrent selectMeteoWeatherCurrentById(Long id);

    /**
     * 查询weatherCurrent列表
     * 
     * @param meteoWeatherCurrent weatherCurrent
     * @return weatherCurrent集合
     */
    public List<MeteoWeatherCurrent> selectMeteoWeatherCurrentList(MeteoWeatherCurrent meteoWeatherCurrent);

    /**
     * 新增weatherCurrent
     * 
     * @param meteoWeatherCurrent weatherCurrent
     * @return 结果
     */
    public int insertMeteoWeatherCurrent(MeteoWeatherCurrent meteoWeatherCurrent);

    /**
     * 修改weatherCurrent
     * 
     * @param meteoWeatherCurrent weatherCurrent
     * @return 结果
     */
    public int updateMeteoWeatherCurrent(MeteoWeatherCurrent meteoWeatherCurrent);

    /**
     * 删除weatherCurrent
     * 
     * @param id weatherCurrent主键
     * @return 结果
     */
    public int deleteMeteoWeatherCurrentById(Long id);

    /**
     * 批量删除weatherCurrent
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMeteoWeatherCurrentByIds(Long[] ids);
}
