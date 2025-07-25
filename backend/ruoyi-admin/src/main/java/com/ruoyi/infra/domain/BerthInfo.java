package com.ruoyi.infra.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * berthInfo对象 bgv_berth_info
 * 
 * @author dt
 * @date 2025-06-26
 */
public class BerthInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    private Long id;

    /** 码头名称 */
    @Excel(name = "码头名称")
    private String name;

    /** ISRS 编号（唯一标识） */
    @Excel(name = "ISRS 编号", readConverterExp = "唯=一标识")
    private String isrs;

    /** 发布单位 */
    private String originator;

    /** 纬度 */
    @Excel(name = "纬度")
    private Long latitude;

    /** 经度 */
    @Excel(name = "经度")
    private Long longitude;

    /** 最后修改时间 */
    private Date lastModification;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setIsrs(String isrs) 
    {
        this.isrs = isrs;
    }

    public String getIsrs() 
    {
        return isrs;
    }

    public void setOriginator(String originator) 
    {
        this.originator = originator;
    }

    public String getOriginator() 
    {
        return originator;
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

    public void setLastModification(Date lastModification) 
    {
        this.lastModification = lastModification;
    }

    public Date getLastModification() 
    {
        return lastModification;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("isrs", getIsrs())
            .append("originator", getOriginator())
            .append("latitude", getLatitude())
            .append("longitude", getLongitude())
            .append("lastModification", getLastModification())
            .toString();
    }
}
