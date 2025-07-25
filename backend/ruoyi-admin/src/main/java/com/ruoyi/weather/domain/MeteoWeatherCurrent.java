package com.ruoyi.weather.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * weatherCurrent对象 meteo_weather_current
 * 
 * @author dt
 * @date 2025-07-03
 */
public class MeteoWeatherCurrent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date recordTime;

    /** 数据时间间隔 (秒) */
    @Excel(name = "数据时间间隔 (秒)")
    private Long intervalSeconds;

    /** 单位: °C */
    @Excel(name = "单位: °C")
    private BigDecimal temperature2m;

    /** 单位: km/h */
    @Excel(name = "单位: km/h")
    private BigDecimal windSpeed10m;

    /** 单位: ° */
    @Excel(name = "单位: °")
    private Long windDirection10m;

    /** 单位: km/h */
    @Excel(name = "单位: km/h")
    private BigDecimal windGusts10m;

    /** 单位: mm */
    @Excel(name = "单位: mm")
    private BigDecimal rain;

    /** 单位: % */
    @Excel(name = "单位: %")
    private Long relativeHumidity2m;

    /** WMO 天气代码 */
    @Excel(name = "WMO 天气代码")
    private Long weatherCode;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private BigDecimal latitude;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private BigDecimal longitude;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private BigDecimal elevation;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String timezone;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long utcOffsetSeconds;

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

    public void setRecordTime(Date recordTime) 
    {
        this.recordTime = recordTime;
    }

    public Date getRecordTime() 
    {
        return recordTime;
    }

    public void setIntervalSeconds(Long intervalSeconds) 
    {
        this.intervalSeconds = intervalSeconds;
    }

    public Long getIntervalSeconds() 
    {
        return intervalSeconds;
    }

    public void setTemperature2m(BigDecimal temperature2m) 
    {
        this.temperature2m = temperature2m;
    }

    public BigDecimal getTemperature2m() 
    {
        return temperature2m;
    }

    public void setWindSpeed10m(BigDecimal windSpeed10m) 
    {
        this.windSpeed10m = windSpeed10m;
    }

    public BigDecimal getWindSpeed10m() 
    {
        return windSpeed10m;
    }

    public void setWindDirection10m(Long windDirection10m) 
    {
        this.windDirection10m = windDirection10m;
    }

    public Long getWindDirection10m() 
    {
        return windDirection10m;
    }

    public void setWindGusts10m(BigDecimal windGusts10m) 
    {
        this.windGusts10m = windGusts10m;
    }

    public BigDecimal getWindGusts10m() 
    {
        return windGusts10m;
    }

    public void setRain(BigDecimal rain) 
    {
        this.rain = rain;
    }

    public BigDecimal getRain() 
    {
        return rain;
    }

    public void setRelativeHumidity2m(Long relativeHumidity2m) 
    {
        this.relativeHumidity2m = relativeHumidity2m;
    }

    public Long getRelativeHumidity2m() 
    {
        return relativeHumidity2m;
    }

    public void setWeatherCode(Long weatherCode) 
    {
        this.weatherCode = weatherCode;
    }

    public Long getWeatherCode() 
    {
        return weatherCode;
    }

    public void setLatitude(BigDecimal latitude) 
    {
        this.latitude = latitude;
    }

    public BigDecimal getLatitude() 
    {
        return latitude;
    }

    public void setLongitude(BigDecimal longitude) 
    {
        this.longitude = longitude;
    }

    public BigDecimal getLongitude() 
    {
        return longitude;
    }

    public void setElevation(BigDecimal elevation) 
    {
        this.elevation = elevation;
    }

    public BigDecimal getElevation() 
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
            .append("recordTime", getRecordTime())
            .append("intervalSeconds", getIntervalSeconds())
            .append("temperature2m", getTemperature2m())
            .append("windSpeed10m", getWindSpeed10m())
            .append("windDirection10m", getWindDirection10m())
            .append("windGusts10m", getWindGusts10m())
            .append("rain", getRain())
            .append("relativeHumidity2m", getRelativeHumidity2m())
            .append("weatherCode", getWeatherCode())
            .append("latitude", getLatitude())
            .append("longitude", getLongitude())
            .append("elevation", getElevation())
            .append("timezone", getTimezone())
            .append("utcOffsetSeconds", getUtcOffsetSeconds())
            .append("createdAt", getCreatedAt())
            .toString();
    }
}
