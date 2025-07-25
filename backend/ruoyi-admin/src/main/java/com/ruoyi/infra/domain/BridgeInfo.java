package com.ruoyi.infra.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * bridgeinfo对象 bgv_bridge_info
 * 
 * @author dt
 * @date 2025-06-26
 */
public class BridgeInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增ID */
    private Long id;

    /** 桥梁名称 */
    @Excel(name = "桥梁名称")
    private String name;

    /** ISRS编号 */
    @Excel(name = "ISRS编号")
    private String isrs;

    /** 数据提供方（机构名称） */
    private String originator;

    /** 纬度 */
    @Excel(name = "纬度")
    private Long latitude;

    /** 经度 */
    @Excel(name = "经度")
    private Long longitude;

    /** 最后更新时间 */
    private Date lastModification;

    /** 创建时间 */
    private Date createdAt;

    /** 更新时间 */
    private Date updatedAt;

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

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
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
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
