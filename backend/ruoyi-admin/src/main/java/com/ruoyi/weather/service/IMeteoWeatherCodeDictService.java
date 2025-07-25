package com.ruoyi.weather.service;

import java.util.List;
import com.ruoyi.weather.domain.MeteoWeatherCodeDict;

/**
 * weatherCodeDictService接口
 * 
 * @author dt
 * @date 2025-07-03
 */
public interface IMeteoWeatherCodeDictService 
{
    /**
     * 查询weatherCodeDict
     * 
     * @param code weatherCodeDict主键
     * @return weatherCodeDict
     */
    public MeteoWeatherCodeDict selectMeteoWeatherCodeDictByCode(Long code);

    /**
     * 查询weatherCodeDict列表
     * 
     * @param meteoWeatherCodeDict weatherCodeDict
     * @return weatherCodeDict集合
     */
    public List<MeteoWeatherCodeDict> selectMeteoWeatherCodeDictList(MeteoWeatherCodeDict meteoWeatherCodeDict);

    /**
     * 新增weatherCodeDict
     * 
     * @param meteoWeatherCodeDict weatherCodeDict
     * @return 结果
     */
    public int insertMeteoWeatherCodeDict(MeteoWeatherCodeDict meteoWeatherCodeDict);

    /**
     * 修改weatherCodeDict
     * 
     * @param meteoWeatherCodeDict weatherCodeDict
     * @return 结果
     */
    public int updateMeteoWeatherCodeDict(MeteoWeatherCodeDict meteoWeatherCodeDict);

    /**
     * 批量删除weatherCodeDict
     * 
     * @param codes 需要删除的weatherCodeDict主键集合
     * @return 结果
     */
    public int deleteMeteoWeatherCodeDictByCodes(Long[] codes);

    /**
     * 删除weatherCodeDict信息
     * 
     * @param code weatherCodeDict主键
     * @return 结果
     */
    public int deleteMeteoWeatherCodeDictByCode(Long code);
}
