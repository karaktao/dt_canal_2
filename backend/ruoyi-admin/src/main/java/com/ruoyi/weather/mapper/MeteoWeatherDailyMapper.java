package com.ruoyi.weather.mapper;

import java.util.Date;
import java.util.List;
import com.ruoyi.weather.domain.MeteoWeatherDaily;
import io.lettuce.core.dynamic.annotation.Param;

/**
 * weatherDailyMapper接口
 * 
 * @author DT
 * @date 2025-07-03
 */
public interface MeteoWeatherDailyMapper 
{
    /**
     * 查询weatherDaily
     * 
     * @param id weatherDaily主键
     * @return weatherDaily
     */
    public MeteoWeatherDaily selectMeteoWeatherDailyById(Long id);


//    /**
//     * 根据日期查询
//     *
//     * @param
//     * @return weatherDaily
//     */
//    public MeteoWeatherDaily selectMeteoWeatherDailyByTime(Date weatherDate);
//


    /**
     * 根据地点，日期查询
     *
     * @param
     * @return weatherDaily
     */
    public MeteoWeatherDaily selectByLocationAndDate(@Param("latitude")    String latitude,
                                                     @Param("longitude")   String longitude,
                                                     @Param("weatherDate") Date   weatherDate);




    /**
     * 查询weatherDaily列表
     * 
     * @param meteoWeatherDaily weatherDaily
     * @return weatherDaily集合
     */
    public List<MeteoWeatherDaily> selectMeteoWeatherDailyList(MeteoWeatherDaily meteoWeatherDaily);

    /**
     * 新增weatherDaily
     * 
     * @param meteoWeatherDaily weatherDaily
     * @return 结果
     */
    public int insertMeteoWeatherDaily(MeteoWeatherDaily meteoWeatherDaily);

    /**
     * 修改weatherDaily
     * 
     * @param meteoWeatherDaily weatherDaily
     * @return 结果
     */
    public int updateMeteoWeatherDaily(MeteoWeatherDaily meteoWeatherDaily);

    /**
     * 删除weatherDaily
     * 
     * @param id weatherDaily主键
     * @return 结果
     */
    public int deleteMeteoWeatherDailyById(Long id);

    /**
     * 批量删除weatherDaily
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMeteoWeatherDailyByIds(Long[] ids);
}
