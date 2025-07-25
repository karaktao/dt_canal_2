package com.ruoyi.weather.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * weatherMinutely对象 meteo_weather_minutely_15
 * 
 * @author dt
 * @date 2025-07-03
 */
public class MeteoWeatherMinutely extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date timestamp;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private BigDecimal temperature2m;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private BigDecimal windSpeed10m;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private BigDecimal precipitation;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long precipitationProbability;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long visibility;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long weatherCode;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private BigDecimal locationLatitude;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private BigDecimal locationLongitude;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private BigDecimal elevation;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String timezone;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setTimestamp(Date timestamp) 
    {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() 
    {
        return timestamp;
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

    public void setPrecipitation(BigDecimal precipitation) 
    {
        this.precipitation = precipitation;
    }

    public BigDecimal getPrecipitation() 
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

    public void setLocationLatitude(BigDecimal locationLatitude) 
    {
        this.locationLatitude = locationLatitude;
    }

    public BigDecimal getLocationLatitude() 
    {
        return locationLatitude;
    }

    public void setLocationLongitude(BigDecimal locationLongitude) 
    {
        this.locationLongitude = locationLongitude;
    }

    public BigDecimal getLocationLongitude() 
    {
        return locationLongitude;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("timestamp", getTimestamp())
            .append("temperature2m", getTemperature2m())
            .append("windSpeed10m", getWindSpeed10m())
            .append("precipitation", getPrecipitation())
            .append("precipitationProbability", getPrecipitationProbability())
            .append("visibility", getVisibility())
            .append("weatherCode", getWeatherCode())
            .append("locationLatitude", getLocationLatitude())
            .append("locationLongitude", getLocationLongitude())
            .append("elevation", getElevation())
            .append("timezone", getTimezone())
            .toString();
    }
}
