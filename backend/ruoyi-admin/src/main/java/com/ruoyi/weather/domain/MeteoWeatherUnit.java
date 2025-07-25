package com.ruoyi.weather.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * weatherUnit对象 meteo_weather_unit
 * 
 * @author dt
 * @date 2025-07-03
 */
public class MeteoWeatherUnit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 数据分类，如 daily, hourly, minutely_15, current */
    @Excel(name = "数据分类，如 daily, hourly, minutely_15, current")
    private String category;

    /** 字段名，例如 temperature_2m */
    @Excel(name = "字段名，例如 temperature_2m")
    private String field;

    /** 单位，例如 ℃、mm */
    @Excel(name = "单位，例如 ℃、mm")
    private String unit;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }

    public void setField(String field) 
    {
        this.field = field;
    }

    public String getField() 
    {
        return field;
    }

    public void setUnit(String unit) 
    {
        this.unit = unit;
    }

    public String getUnit() 
    {
        return unit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("category", getCategory())
            .append("field", getField())
            .append("unit", getUnit())
            .toString();
    }
}
