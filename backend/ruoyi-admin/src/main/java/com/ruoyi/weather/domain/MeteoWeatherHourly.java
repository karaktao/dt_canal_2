package com.ruoyi.weather.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * weatherHourly对象 meteo_weather_hourly
 * 
 * @author dt
 * @date 2025-07-03
 */
public class MeteoWeatherHourly extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long latitude;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long longitude;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long elevation;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String timezone;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long utcOffsetSeconds;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date recordTime;

    /** 单位: °C */
    @Excel(name = "单位: °C")
    private Long temperature2m;

    /** 单位: km/h */
    @Excel(name = "单位: km/h")
    private Long windSpeed10m;

    /** 单位: mm */
    @Excel(name = "单位: mm")
    private Long precipitation;

    /** 单位: % */
    @Excel(name = "单位: %")
    private Long precipitationProbability;

    /** 单位: m */
    @Excel(name = "单位: m")
    private Long visibility;

    /** WMO 天气代码 */
    @Excel(name = "WMO 天气代码")
    private Long weatherCode;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date createdAt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setLatitude(Long latitude) 
    {
        this.latitude = latitude;
    }

    public Long getLatitude() 
    {
        return latitude;
    }

    public void setLongitude(Long longitude) 
    {
        this.longitude = longitude;
    }

    public Long getLongitude() 
    {
        return longitude;
    }

    public void setElevation(Long elevation) 
    {
        this.elevation = elevation;
    }

    public Long getElevation() 
    {
        return elevation;
    }

    public void setTimezone(String timezone) 
    {
        this.timezone = timezone;
    }

    public String getTimezone() 
    {
        return timezone;
    }

    public void setUtcOffsetSeconds(Long utcOffsetSeconds) 
    {
        this.utcOffsetSeconds = utcOffsetSeconds;
    }

    public Long getUtcOffsetSeconds() 
    {
        return utcOffsetSeconds;
    }

    public void setRecordTime(Date recordTime) 
    {
        this.recordTime = recordTime;
    }

    public Date getRecordTime() 
    {
        return recordTime;
    }

    public void setTemperature2m(Long temperature2m) 
    {
        this.temperature2m = temperature2m;
    }

    public Long getTemperature2m() 
    {
        return temperature2m;
    }

    public void setWindSpeed10m(Long windSpeed10m) 
    {
        this.windSpeed10m = windSpeed10m;
    }

    public Long getWindSpeed10m() 
    {
        return windSpeed10m;
    }

    public void setPrecipitation(Long precipitation) 
    {
        this.precipitation = precipitation;
    }

    public Long getPrecipitation() 
    {
        return precipitation;
    }

    public void setPrecipitationProbability(Long precipitationProbability) 
    {
        this.precipitationProbability = precipitationProbability;
    }

    public Long getPrecipitationProbability() 
    {
        return precipitationProbability;
    }

    public void setVisibility(Long visibility) 
    {
        this.visibility = visibility;
    }

    public Long getVisibility() 
    {
        return visibility;
    }

    public void setWeatherCode(Long weatherCode) 
    {
        this.weatherCode = weatherCode;
    }

    public Long getWeatherCode() 
    {
        return weatherCode;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("latitude", getLatitude())
            .append("longitude", getLongitude())
            .append("elevation", getElevation())
            .append("timezone", getTimezone())
            .append("utcOffsetSeconds", getUtcOffsetSeconds())
            .append("recordTime", getRecordTime())
            .append("temperature2m", getTemperature2m())
            .append("windSpeed10m", getWindSpeed10m())
            .append("precipitation", getPrecipitation())
            .append("precipitationProbability", getPrecipitationProbability())
            .append("visibility", getVisibility())
            .append("weatherCode", getWeatherCode())
            .append("createdAt", getCreatedAt())
            .toString();
    }
}
