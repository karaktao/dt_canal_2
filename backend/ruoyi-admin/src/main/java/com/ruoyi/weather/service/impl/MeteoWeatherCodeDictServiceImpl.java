package com.ruoyi.weather.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weather.mapper.MeteoWeatherCodeDictMapper;
import com.ruoyi.weather.domain.MeteoWeatherCodeDict;
import com.ruoyi.weather.service.IMeteoWeatherCodeDictService;

/**
 * weatherCodeDictService业务层处理
 * 
 * @author dt
 * @date 2025-07-03
 */
@Service
public class MeteoWeatherCodeDictServiceImpl implements IMeteoWeatherCodeDictService 
{
    @Autowired
    private MeteoWeatherCodeDictMapper meteoWeatherCodeDictMapper;

    /**
     * 查询weatherCodeDict
     * 
     * @param code weatherCodeDict主键
     * @return weatherCodeDict
     */
    @Override
    public MeteoWeatherCodeDict selectMeteoWeatherCodeDictByCode(Long code)
    {
        return meteoWeatherCodeDictMapper.selectMeteoWeatherCodeDictByCode(code);
    }

    /**
     * 查询weatherCodeDict列表
     * 
     * @param meteoWeatherCodeDict weatherCodeDict
     * @return weatherCodeDict
     */
    @Override
    public List<MeteoWeatherCodeDict> selectMeteoWeatherCodeDictList(MeteoWeatherCodeDict meteoWeatherCodeDict)
    {
        return meteoWeatherCodeDictMapper.selectMeteoWeatherCodeDictList(meteoWeatherCodeDict);
    }

    /**
     * 新增weatherCodeDict
     * 
     * @param meteoWeatherCodeDict weatherCodeDict
     * @return 结果
     */
    @Override
    public int insertMeteoWeatherCodeDict(MeteoWeatherCodeDict meteoWeatherCodeDict)
    {
        return meteoWeatherCodeDictMapper.insertMeteoWeatherCodeDict(meteoWeatherCodeDict);
    }

    /**
     * 修改weatherCodeDict
     * 
     * @param meteoWeatherCodeDict weatherCodeDict
     * @return 结果
     */
    @Override
    public int updateMeteoWeatherCodeDict(MeteoWeatherCodeDict meteoWeatherCodeDict)
    {
        return meteoWeatherCodeDictMapper.updateMeteoWeatherCodeDict(meteoWeatherCodeDict);
    }

    /**
     * 批量删除weatherCodeDict
     * 
     * @param codes 需要删除的weatherCodeDict主键
     * @return 结果
     */
    @Override
    public int deleteMeteoWeatherCodeDictByCodes(Long[] codes)
    {
        return meteoWeatherCodeDictMapper.deleteMeteoWeatherCodeDictByCodes(codes);
    }

    /**
     * 删除weatherCodeDict信息
     * 
     * @param code weatherCodeDict主键
     * @return 结果
     */
    @Override
    public int deleteMeteoWeatherCodeDictByCode(Long code)
    {
        return meteoWeatherCodeDictMapper.deleteMeteoWeatherCodeDictByCode(code);
    }
}
