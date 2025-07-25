package com.ruoyi.weather.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * weatherCodeDict对象 meteo_weather_code_dict
 * 
 * @author dt
 * @date 2025-07-03
 */
public class MeteoWeatherCodeDict extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long code;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String descriptionEn;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String descriptionCn;

    public void setCode(Long code) 
    {
        this.code = code;
    }

    public Long getCode() 
    {
        return code;
    }

    public void setDescriptionEn(String descriptionEn) 
    {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionEn() 
    {
        return descriptionEn;
    }

    public void setDescriptionCn(String descriptionCn) 
    {
        this.descriptionCn = descriptionCn;
    }

    public String getDescriptionCn() 
    {
        return descriptionCn;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("code", getCode())
            .append("descriptionEn", getDescriptionEn())
            .append("descriptionCn", getDescriptionCn())
            .toString();
    }
}
