package com.ruoyi.weather.mapper;

import java.util.List;
import com.ruoyi.weather.domain.MeteoWeatherCodeDict;
import com.ruoyi.weather.domain.MeteoWeatherUnit;

/**
 * weatherCodeDictMapper接口
 * 
 * @author dt
 * @date 2025-07-03
 */
public interface MeteoWeatherCodeDictMapper 
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
     * 删除weatherCodeDict
     * 
     * @param code weatherCodeDict主键
     * @return 结果
     */
    public int deleteMeteoWeatherCodeDictByCode(Long code);

    /**
     * 批量删除weatherCodeDict
     * 
     * @param codes 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMeteoWeatherCodeDictByCodes(Long[] codes);

    /**
     * weatherUnitMapper接口
     *
     * @author dt
     * @date 2025-07-03
     */
    interface MeteoWeatherUnitMapper
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
         * 删除weatherUnit
         *
         * @param id weatherUnit主键
         * @return 结果
         */
        public int deleteMeteoWeatherUnitById(Long id);

        /**
         * 批量删除weatherUnit
         *
         * @param ids 需要删除的数据主键集合
         * @return 结果
         */
        public int deleteMeteoWeatherUnitByIds(Long[] ids);
    }
}
