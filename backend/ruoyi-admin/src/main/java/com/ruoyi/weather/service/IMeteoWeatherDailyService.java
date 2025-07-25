package com.ruoyi.weather.service;

import java.util.Date;
import java.util.List;

import com.github.pagehelper.ISelect;
import com.ruoyi.weather.domain.MeteoWeatherDaily;

/**
 * weatherDailyService接口
 * 
 * @author DT
 * @date 2025-07-03
 */
public interface IMeteoWeatherDailyService 
{
    /**
     * 查询weatherDaily
     * 
     * @param id weatherDaily主键
     * @return weatherDaily
     */
    public MeteoWeatherDaily selectMeteoWeatherDailyById(Long id);

//
//    /**
//     * 根据日期查询
//     *
//     * @param   time 日期
//     * @return weatherDaily
//     */
//    public MeteoWeatherDaily selectMeteoWeatherDailyByTime(Date time);


    /**
     * 根据地点日期查询
     *
     * @param
     * @return weatherDaily
     */
    public MeteoWeatherDaily selectByLocationAndDate(String latitude, String longitude, Date time);



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
     * 新增批量加入weatherDaily
     *
     * @param list weatherDaily集合
     */

    void insertDailyBatch(List<MeteoWeatherDaily> list);



    /**
     * 修改weatherDaily
     * 
     * @param meteoWeatherDaily weatherDaily
     * @return 结果
     */
    public int updateMeteoWeatherDaily(MeteoWeatherDaily meteoWeatherDaily);

    /**
     * 批量删除weatherDaily
     * 
     * @param ids 需要删除的weatherDaily主键集合
     * @return 结果
     */
    public int deleteMeteoWeatherDailyByIds(Long[] ids);

    /**
     * 删除weatherDaily信息
     * 
     * @param id weatherDaily主键
     * @return 结果
     */
    public int deleteMeteoWeatherDailyById(Long id);
}
