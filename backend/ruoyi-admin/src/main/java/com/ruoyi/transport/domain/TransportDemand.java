package com.ruoyi.transport.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 物流发布对象 dt_transport_demand
 *
 * @author dt
 * @date 2025-06-25
 */
public class TransportDemand extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long id;

    /** 所属租户ID */
    @Excel(name = "所属租户ID")
    private Long tenantId;

    /** 指派类型 */
    @Excel(name = "指派类型")
    private String assignmentType;

    /** 发布者ID */
    private Long publishedBy;

    /** 发布时间 */
    private Date publishedAt;

    /** 创建时间 */
    private Date createdAt;

    /** 指派时间 */
    private Date assignedAt;

    /** 货物类型 */
    @Excel(name = "货物类型")
    private String cargoType;

    /** 所需吨位 */
    @Excel(name = "所需吨位")
    private BigDecimal tonnageDemand;

    /** 所需体积 */
    @Excel(name = "所需体积")
    private BigDecimal volumeDemand;

    /** 所需集装箱数 */
    @Excel(name = "所需集装箱数")
    private String containerDemand;

    /** 船舶ID */
    @Excel(name = "船舶ID")
    private Long vesselId;

    /** MMSI编号 */
    @Excel(name = "MMSI编号")
    private String mmsiNumber;

    /** 船名 */
    @Excel(name = "船名")
    private String vesselName;

    /** 是否空船 */
    @Excel(name = "是否空船")
    private Integer isEmptyVessel;

    /** 可用吨位 */
    @Excel(name = "可用吨位")
    private BigDecimal tonnageAvailable;

    /** 可用体积 */
    @Excel(name = "可用体积")
    private BigDecimal volumeAvailable;

    /** 可用集装箱数 */
    @Excel(name = "可用集装箱数")
    private String containerAvailable;

    /** 是否返程 */
    @Excel(name = "是否返程")
    private Long isReturnTripAvailable;

    /** 船舶可用时间段 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "船舶可用时间段", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date vesselAvailabilityStart;

    /** 船舶可用时间段 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "船舶可用时间段", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date vesselAvailabilityEnd;

    /** 到达港口 */
    @Excel(name = "到达港口")
    private String returnDestinationPort;

    /** 到达港口 */
    @Excel(name = "到达港口")
    private String returnDestinationPortId;

    /** 出发港口 */
    @Excel(name = "出发港口")
    private String originPort;

    /** 出发港口 */
    @Excel(name = "出发港口")
    private String originPortId;

    /** 到达港口 */
    @Excel(name = "到达港口")
    private String destinationPort;

    /** 到达港口 */
    @Excel(name = "到达港口")
    private String destinationPortId;

    /** 出发时间起 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "出发时间起", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date departureStart;

    /** 出发时间止 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "出发时间止", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date departureEnd;

    /** 到达时间起 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "到达时间起", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date arrivalStart;

    /** 到达时间止 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "到达时间止", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date arrivalEnd;

    /** 预计到达时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "预计到达时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date arrivalEstimate;

    /** 装货时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "装货时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    /** 卸货时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "卸货时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date unloadTime;

    /** 优先级设定 */
    @Excel(name = "优先级设定")
    private String prioritySetting;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 经停港口 */
    @Excel(name = "经停港口")
    private String intermediatePorts;

    /** 地理路径 */
    private String geoPath;

    /** 经停港口 */
    @Excel(name = "经停港口")
    private String intermediatePortsId;

    /** 货物特性 */
    @Excel(name = "货物特性")
    private String cargoProperty;


    // ←—— 在这里添加两个新的属性
    /** 始发城市 */
    @Excel(name = "始发城市")
    private String originCity;          // ← 新增

    /** 目的城市 */
    @Excel(name = "目的城市")
    private String destinationCity;     // ← 新增

    // —— 新增这两段 getter / setter —— //
    public String getOriginCity()
    {
        return originCity;
    }

    public void setOriginCity(String originCity)
    {
        this.originCity = originCity;
    }

    public String getDestinationCity()
    {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity)
    {
        this.destinationCity = destinationCity;
    }

    //最后还需append


    /**  */
    @Excel(name = "")
    private Long isMerge;

    /**  */
    @Excel(name = "")
    private Long isTransshipment;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long containerId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String remarks;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setTenantId(Long tenantId)
    {
        this.tenantId = tenantId;
    }

    public Long getTenantId()
    {
        return tenantId;
    }

    public void setAssignmentType(String assignmentType)
    {
        this.assignmentType = assignmentType;
    }

    public String getAssignmentType()
    {
        return assignmentType;
    }

    public void setPublishedBy(Long publishedBy)
    {
        this.publishedBy = publishedBy;
    }

    public Long getPublishedBy()
    {
        return publishedBy;
    }

    public void setPublishedAt(Date publishedAt)
    {
        this.publishedAt = publishedAt;
    }

    public Date getPublishedAt()
    {
        return publishedAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setAssignedAt(Date assignedAt)
    {
        this.assignedAt = assignedAt;
    }

    public Date getAssignedAt()
    {
        return assignedAt;
    }

    public void setCargoType(String cargoType)
    {
        this.cargoType = cargoType;
    }

    public String getCargoType()
    {
        return cargoType;
    }

    public void setTonnageDemand(BigDecimal tonnageDemand)
    {
        this.tonnageDemand = tonnageDemand;
    }

    public BigDecimal getTonnageDemand()
    {
        return tonnageDemand;
    }

    public void setVolumeDemand(BigDecimal volumeDemand)
    {
        this.volumeDemand = volumeDemand;
    }

    public BigDecimal getVolumeDemand()
    {
        return volumeDemand;
    }

    public void setContainerDemand(String containerDemand)
    {
        this.containerDemand = containerDemand;
    }

    public String getContainerDemand()
    {
        return containerDemand;
    }

    public void setVesselId(Long vesselId)
    {
        this.vesselId = vesselId;
    }

    public Long getVesselId()
    {
        return vesselId;
    }

    public void setMmsiNumber(String mmsiNumber)
    {
        this.mmsiNumber = mmsiNumber;
    }

    public String getMmsiNumber()
    {
        return mmsiNumber;
    }

    public void setVesselName(String vesselName)
    {
        this.vesselName = vesselName;
    }

    public String getVesselName()
    {
        return vesselName;
    }

    public void setIsEmptyVessel(Integer isEmptyVessel)
    {
        this.isEmptyVessel = isEmptyVessel;
    }

    public Integer getIsEmptyVessel()
    {
        return isEmptyVessel;
    }

    public void setTonnageAvailable(BigDecimal tonnageAvailable)
    {
        this.tonnageAvailable = tonnageAvailable;
    }

    public BigDecimal getTonnageAvailable()
    {
        return tonnageAvailable;
    }

    public void setVolumeAvailable(BigDecimal volumeAvailable)
    {
        this.volumeAvailable = volumeAvailable;
    }

    public BigDecimal getVolumeAvailable()
    {
        return volumeAvailable;
    }

    public void setContainerAvailable(String containerAvailable)
    {
        this.containerAvailable = containerAvailable;
    }

    public String getContainerAvailable()
    {
        return containerAvailable;
    }

    public void setIsReturnTripAvailable(Long isReturnTripAvailable)
    {
        this.isReturnTripAvailable = isReturnTripAvailable;
    }

    public Long getIsReturnTripAvailable()
    {
        return isReturnTripAvailable;
    }

    public void setVesselAvailabilityStart(Date vesselAvailabilityStart)
    {
        this.vesselAvailabilityStart = vesselAvailabilityStart;
    }

    public Date getVesselAvailabilityStart()
    {
        return vesselAvailabilityStart;
    }

    public void setVesselAvailabilityEnd(Date vesselAvailabilityEnd)
    {
        this.vesselAvailabilityEnd = vesselAvailabilityEnd;
    }

    public Date getVesselAvailabilityEnd()
    {
        return vesselAvailabilityEnd;
    }

    public void setReturnDestinationPort(String returnDestinationPort)
    {
        this.returnDestinationPort = returnDestinationPort;
    }

    public String getReturnDestinationPort()
    {
        return returnDestinationPort;
    }

    public void setReturnDestinationPortId(String returnDestinationPortId)
    {
        this.returnDestinationPortId = returnDestinationPortId;
    }

    public String getReturnDestinationPortId()
    {
        return returnDestinationPortId;
    }

    public void setOriginPort(String originPort)
    {
        this.originPort = originPort;
    }

    public String getOriginPort()
    {
        return originPort;
    }

    public void setOriginPortId(String originPortId)
    {
        this.originPortId = originPortId;
    }

    public String getOriginPortId()
    {
        return originPortId;
    }

    public void setDestinationPort(String destinationPort)
    {
        this.destinationPort = destinationPort;
    }

    public String getDestinationPort()
    {
        return destinationPort;
    }

    public void setDestinationPortId(String destinationPortId)
    {
        this.destinationPortId = destinationPortId;
    }

    public String getDestinationPortId()
    {
        return destinationPortId;
    }

    public void setDepartureStart(Date departureStart)
    {
        this.departureStart = departureStart;
    }

    public Date getDepartureStart()
    {
        return departureStart;
    }

    public void setDepartureEnd(Date departureEnd)
    {
        this.departureEnd = departureEnd;
    }

    public Date getDepartureEnd()
    {
        return departureEnd;
    }

    public void setArrivalStart(Date arrivalStart)
    {
        this.arrivalStart = arrivalStart;
    }

    public Date getArrivalStart()
    {
        return arrivalStart;
    }

    public void setArrivalEnd(Date arrivalEnd)
    {
        this.arrivalEnd = arrivalEnd;
    }

    public Date getArrivalEnd()
    {
        return arrivalEnd;
    }

    public void setArrivalEstimate(Date arrivalEstimate)
    {
        this.arrivalEstimate = arrivalEstimate;
    }

    public Date getArrivalEstimate()
    {
        return arrivalEstimate;
    }

    public void setUploadTime(Date uploadTime)
    {
        this.uploadTime = uploadTime;
    }

    public Date getUploadTime()
    {
        return uploadTime;
    }

    public void setUnloadTime(Date unloadTime)
    {
        this.unloadTime = unloadTime;
    }

    public Date getUnloadTime()
    {
        return unloadTime;
    }

    public void setPrioritySetting(String prioritySetting)
    {
        this.prioritySetting = prioritySetting;
    }

    public String getPrioritySetting()
    {
        return prioritySetting;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setIntermediatePorts(String intermediatePorts)
    {
        this.intermediatePorts = intermediatePorts;
    }

    public String getIntermediatePorts()
    {
        return intermediatePorts;
    }

    public void setGeoPath(String geoPath)
    {
        this.geoPath = geoPath;
    }

    public String getGeoPath()
    {
        return geoPath;
    }

    public void setIntermediatePortsId(String intermediatePortsId)
    {
        this.intermediatePortsId = intermediatePortsId;
    }

    public String getIntermediatePortsId()
    {
        return intermediatePortsId;
    }

    public void setCargoProperty(String cargoProperty)
    {
        this.cargoProperty = cargoProperty;
    }

    public String getCargoProperty()
    {
        return cargoProperty;
    }

    public void setIsMerge(Long isMerge)
    {
        this.isMerge = isMerge;
    }

    public Long getIsMerge()
    {
        return isMerge;
    }

    public void setIsTransshipment(Long isTransshipment)
    {
        this.isTransshipment = isTransshipment;
    }

    public Long getIsTransshipment()
    {
        return isTransshipment;
    }

    public void setContainerId(Long containerId)
    {
        this.containerId = containerId;
    }

    public Long getContainerId()
    {
        return containerId;
    }

    public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }

    public String getRemarks()
    {
        return remarks;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("tenantId", getTenantId())
            .append("assignmentType", getAssignmentType())
            .append("publishedBy", getPublishedBy())
            .append("publishedAt", getPublishedAt())
            .append("createdAt", getCreatedAt())
            .append("assignedAt", getAssignedAt())
            .append("cargoType", getCargoType())
            .append("tonnageDemand", getTonnageDemand())
            .append("volumeDemand", getVolumeDemand())
            .append("containerDemand", getContainerDemand())
            .append("vesselId", getVesselId())
            .append("mmsiNumber", getMmsiNumber())
            .append("vesselName", getVesselName())
            .append("isEmptyVessel", getIsEmptyVessel())
            .append("tonnageAvailable", getTonnageAvailable())
            .append("volumeAvailable", getVolumeAvailable())
            .append("containerAvailable", getContainerAvailable())
            .append("isReturnTripAvailable", getIsReturnTripAvailable())
            .append("vesselAvailabilityStart", getVesselAvailabilityStart())
            .append("vesselAvailabilityEnd", getVesselAvailabilityEnd())
            .append("returnDestinationPort", getReturnDestinationPort())
            .append("returnDestinationPortId", getReturnDestinationPortId())
            .append("originPort", getOriginPort())
            .append("originPortId", getOriginPortId())
            .append("destinationPort", getDestinationPort())
            .append("destinationPortId", getDestinationPortId())
            .append("departureStart", getDepartureStart())
            .append("departureEnd", getDepartureEnd())
            .append("arrivalStart", getArrivalStart())
            .append("arrivalEnd", getArrivalEnd())
            .append("arrivalEstimate", getArrivalEstimate())
            .append("uploadTime", getUploadTime())
            .append("unloadTime", getUnloadTime())
            .append("prioritySetting", getPrioritySetting())
            .append("status", getStatus())
            .append("intermediatePorts", getIntermediatePorts())
            .append("geoPath", getGeoPath())
            .append("intermediatePortsId", getIntermediatePortsId())
            .append("cargoProperty", getCargoProperty())
            .append("isMerge", getIsMerge())
            .append("isTransshipment", getIsTransshipment())
            .append("containerId", getContainerId())
            .append("remarks", getRemarks())
                .append("originCity", getOriginCity())
                .append("destinationCity", getDestinationCity())
            .toString();
    }
}
