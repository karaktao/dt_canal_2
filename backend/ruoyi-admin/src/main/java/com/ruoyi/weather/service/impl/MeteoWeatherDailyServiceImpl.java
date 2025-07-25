package com.ruoyi.weather.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weather.mapper.MeteoWeatherDailyMapper;
import com.ruoyi.weather.domain.MeteoWeatherDaily;
import com.ruoyi.weather.service.IMeteoWeatherDailyService;

/**
 * weatherDailyService业务层处理
 * 
 * @author DT
 * @date 2025-07-03
 */
@Service
public class MeteoWeatherDailyServiceImpl implements IMeteoWeatherDailyService 
{
    @Autowired
    private MeteoWeatherDailyMapper meteoWeatherDailyMapper;

    /**
     * 查询weatherDaily
     * 
     * @param id weatherDaily主键
     * @return weatherDaily
     */
    @Override
    public MeteoWeatherDaily selectMeteoWeatherDailyById(Long id)
    {
        return meteoWeatherDailyMapper.selectMeteoWeatherDailyById(id);
    }


//    /**
//     * 通过时间查询
//     *
//     * @param time 时间
//     * @return weatherDaily
//     */
//    @Override
//    public MeteoWeatherDaily selectMeteoWeatherDailyByTime(Date time) {
//        return meteoWeatherDailyMapper.selectMeteoWeatherDailyByTime(time);
//    }


    /**
     * 通过地点时间查询
     *
     * @param
     * @return weatherDaily
     */
    @Override
    public MeteoWeatherDaily selectByLocationAndDate(String latitude, String longitude, Date time) {
        return meteoWeatherDailyMapper.selectByLocationAndDate(latitude, longitude, time);
    }





    /**
     * 查询weatherDaily列表
     * 
     * @param meteoWeatherDaily weatherDaily
     * @return weatherDaily
     */
    @Override
    public List<MeteoWeatherDaily> selectMeteoWeatherDailyList(MeteoWeatherDaily meteoWeatherDaily)
    {
        return meteoWeatherDailyMapper.selectMeteoWeatherDailyList(meteoWeatherDaily);
    }

    /**
     * 新增weatherDaily
     * 
     * @param meteoWeatherDaily weatherDaily
     * @return 结果
     */
    @Override
    public int insertMeteoWeatherDaily(MeteoWeatherDaily meteoWeatherDaily)
    {
        return meteoWeatherDailyMapper.insertMeteoWeatherDaily(meteoWeatherDaily);
    }


    /**
     * 批量新增weatherDaily
     *
     * @param list weatherDaily列表
     */
    @Override
    public void insertDailyBatch(List<MeteoWeatherDaily> list) {
        for (MeteoWeatherDaily item : list) {
            insertMeteoWeatherDaily(item);
        }
    }




    /**
     * 修改weatherDaily
     * 
     * @param meteoWeatherDaily weatherDaily
     * @return 结果
     */
    @Override
    public int updateMeteoWeatherDaily(MeteoWeatherDaily meteoWeatherDaily)
    {
        return meteoWeatherDailyMapper.updateMeteoWeatherDaily(meteoWeatherDaily);
    }

    /**
     * 批量删除weatherDaily
     * 
     * @param ids 需要删除的weatherDaily主键
     * @return 结果
     */
    @Override
    public int deleteMeteoWeatherDailyByIds(Long[] ids)
    {
        return meteoWeatherDailyMapper.deleteMeteoWeatherDailyByIds(ids);
    }

    /**
     * 删除weatherDaily信息
     * 
     * @param id weatherDaily主键
     * @return 结果
     */
    @Override
    public int deleteMeteoWeatherDailyById(Long id)
    {
        return meteoWeatherDailyMapper.deleteMeteoWeatherDailyById(id);
    }
}
