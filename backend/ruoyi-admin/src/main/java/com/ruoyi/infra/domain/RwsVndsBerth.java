package com.ruoyi.infra.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * VNDS-berth对象 rws_VNDS_berth
 * 
 * @author dt
 * @date 2025-07-25
 */
public class RwsVndsBerth extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一ID */
    private Long Id;

    /** 地理类型 */
    @Excel(name = "地理类型")
    private String GeoType;

    /** 地理版本号 */
    @Excel(name = "地理版本号")
    private Long GeoGeneration;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String GeoJSON;

    /** 名称 */
    @Excel(name = "名称")
    private String Name;

    /** 几何信息（WKT） */
    @Excel(name = "几何信息", readConverterExp = "W=KT")
    private String Geometry;

    /** VIN码 */
    @Excel(name = "VIN码")
    private String VinCode;

    /** 联合国地点代码 */
    @Excel(name = "联合国地点代码")
    private String UnLocationCode;

    /** 备注 */
    @Excel(name = "备注")
    private String Note;

    /** 航线ID */
    @Excel(name = "航线ID")
    private Long RouteId;

    /** 起点公里数 */
    @Excel(name = "起点公里数")
    private Long RouteKmBegin;

    /** 终点公里数 */
    @Excel(name = "终点公里数")
    private Long RouteKmEnd;

    /** 城市 */
    @Excel(name = "城市")
    private String City;

    /** 行政机构ID */
    @Excel(name = "行政机构ID")
    private Long AdministrationId;

    /** 航道段ID */
    @Excel(name = "航道段ID")
    private Long FairwaySectionId;

    /** 航道ID */
    @Excel(name = "航道ID")
    private Long FairwayId;

    /** ISRS ID */
    @Excel(name = "ISRS ID")
    private Long IsrsId;

    /** 状况 */
    @Excel(name = "状况")
    private String Condition;

    /** 分类 */
    @Excel(name = "分类")
    private String Category;

    /** 宽度方向（L/R） */
    @Excel(name = "宽度方向", readConverterExp = "L=/R")
    private String WidthPosition;

    /** 是否有起重机 */
    @Excel(name = "是否有起重机")
    private Integer HasCrane;

    /** 终端ID */
    @Excel(name = "终端ID")
    private Long TerminalId;

    /** 地址 */
    @Excel(name = "地址")
    private String Address;

    /** 社区 */
    @Excel(name = "社区")
    private String Community;

    /** 可达性 */
    @Excel(name = "可达性")
    private String Accessibility;

    /** 可用高度 */
    @Excel(name = "可用高度")
    private Long AvailableHeight;

    /** 可用长度 */
    @Excel(name = "可用长度")
    private Long AvailableLength;

    /** 可用宽度 */
    @Excel(name = "可用宽度")
    private Long AvailableWidth;

    /** 建筑材料 */
    @Excel(name = "建筑材料")
    private String ConstructionMaterial;

    /** 外部编码 */
    @Excel(name = "外部编码")
    private String ForeignCode;

    /** 泊位长度 */
    @Excel(name = "泊位长度")
    private Long Length;

    /** 最大船舶长度 */
    @Excel(name = "最大船舶长度")
    private Long MaximalShipLength;

    /** 最大船队高度 */
    @Excel(name = "最大船队高度")
    private Long MaximumConvoyHeight;

    /** 最大船队长度 */
    @Excel(name = "最大船队长度")
    private Long MaximumConvoyLength;

    /** 最大船队宽度 */
    @Excel(name = "最大船队宽度")
    private Long MaximumConvoyWidth;

    /** 最大吃水深度 */
    @Excel(name = "最大吃水深度")
    private Long MaximumDraught;

    /** 最大船舶高度 */
    @Excel(name = "最大船舶高度")
    private Long MaximumShipHeight;

    /** 最大船舶宽度 */
    @Excel(name = "最大船舶宽度")
    private Long MaximumShipWidth;

    /** 最大停泊时间（小时） */
    @Excel(name = "最大停泊时间", readConverterExp = "小=时")
    private Long MaximumStayDurationH;

    /** 最小船舶长度 */
    @Excel(name = "最小船舶长度")
    private Long MinimalShipLength;

    /** 系泊设施描述 */
    @Excel(name = "系泊设施描述")
    private String MooringFacilitiesDesc;

    /** 系泊设施数量 */
    @Excel(name = "系泊设施数量")
    private Long NumberOfMooringFacilities;

    /** 泊位排数 */
    @Excel(name = "泊位排数")
    private Long NumberOfRows;

    /** 港口区域ID */
    @Excel(name = "港口区域ID")
    private Long PortAreaId;

    /** 码头质量 */
    @Excel(name = "码头质量")
    private String QuayQuality;

    /** 旋转角度 */
    @Excel(name = "旋转角度")
    private Long Rotation;

    /** 岸线类型 */
    @Excel(name = "岸线类型")
    private String ShorelineCategory;

    /** 信号标志 */
    @Excel(name = "信号标志")
    private String Signaling;

    /** 信号标志（别名） */
    @Excel(name = "信号标志", readConverterExp = "别=名")
    private String Signalling;

    /** 运输可能描述 */
    @Excel(name = "运输可能描述")
    private String TransportPossibilitiesDesc;

    /** 使用条件 */
    @Excel(name = "使用条件")
    private String UsageConditions;

    /** 泊位宽度 */
    @Excel(name = "泊位宽度")
    private Long Width;

    /** 设施列表 */
    @Excel(name = "设施列表")
    private String Facility;

    /** 船舶分类 */
    @Excel(name = "船舶分类")
    private String ShipCategories;

    /** 状态列表 */
    @Excel(name = "状态列表")
    private String Status;

    /** 转运货物列表 */
    @Excel(name = "转运货物列表")
    private String TransshipmentGoods;

    public void setId(Long Id) 
    {
        this.Id = Id;
    }

    public Long getId() 
    {
        return Id;
    }

    public void setGeoType(String GeoType) 
    {
        this.GeoType = GeoType;
    }

    public String getGeoType() 
    {
        return GeoType;
    }

    public void setGeoGeneration(Long GeoGeneration) 
    {
        this.GeoGeneration = GeoGeneration;
    }

    public Long getGeoGeneration() 
    {
        return GeoGeneration;
    }

    public void setGeoJSON(String GeoJSON) 
    {
        this.GeoJSON = GeoJSON;
    }

    public String getGeoJSON() 
    {
        return GeoJSON;
    }

    public void setName(String Name) 
    {
        this.Name = Name;
    }

    public String getName() 
    {
        return Name;
    }

    public void setGeometry(String Geometry) 
    {
        this.Geometry = Geometry;
    }

    public String getGeometry() 
    {
        return Geometry;
    }

    public void setVinCode(String VinCode) 
    {
        this.VinCode = VinCode;
    }

    public String getVinCode() 
    {
        return VinCode;
    }

    public void setUnLocationCode(String UnLocationCode) 
    {
        this.UnLocationCode = UnLocationCode;
    }

    public String getUnLocationCode() 
    {
        return UnLocationCode;
    }

    public void setNote(String Note) 
    {
        this.Note = Note;
    }

    public String getNote() 
    {
        return Note;
    }

    public void setRouteId(Long RouteId) 
    {
        this.RouteId = RouteId;
    }

    public Long getRouteId() 
    {
        return RouteId;
    }

    public void setRouteKmBegin(Long RouteKmBegin) 
    {
        this.RouteKmBegin = RouteKmBegin;
    }

    public Long getRouteKmBegin() 
    {
        return RouteKmBegin;
    }

    public void setRouteKmEnd(Long RouteKmEnd) 
    {
        this.RouteKmEnd = RouteKmEnd;
    }

    public Long getRouteKmEnd() 
    {
        return RouteKmEnd;
    }

    public void setCity(String City) 
    {
        this.City = City;
    }

    public String getCity() 
    {
        return City;
    }

    public void setAdministrationId(Long AdministrationId) 
    {
        this.AdministrationId = AdministrationId;
    }

    public Long getAdministrationId() 
    {
        return AdministrationId;
    }

    public void setFairwaySectionId(Long FairwaySectionId) 
    {
        this.FairwaySectionId = FairwaySectionId;
    }

    public Long getFairwaySectionId() 
    {
        return FairwaySectionId;
    }

    public void setFairwayId(Long FairwayId) 
    {
        this.FairwayId = FairwayId;
    }

    public Long getFairwayId() 
    {
        return FairwayId;
    }

    public void setIsrsId(Long IsrsId) 
    {
        this.IsrsId = IsrsId;
    }

    public Long getIsrsId() 
    {
        return IsrsId;
    }

    public void setCondition(String Condition) 
    {
        this.Condition = Condition;
    }

    public String getCondition() 
    {
        return Condition;
    }

    public void setCategory(String Category) 
    {
        this.Category = Category;
    }

    public String getCategory() 
    {
        return Category;
    }

    public void setWidthPosition(String WidthPosition) 
    {
        this.WidthPosition = WidthPosition;
    }

    public String getWidthPosition() 
    {
        return WidthPosition;
    }

    public void setHasCrane(Integer HasCrane) 
    {
        this.HasCrane = HasCrane;
    }

    public Integer getHasCrane() 
    {
        return HasCrane;
    }

    public void setTerminalId(Long TerminalId) 
    {
        this.TerminalId = TerminalId;
    }

    public Long getTerminalId() 
    {
        return TerminalId;
    }

    public void setAddress(String Address) 
    {
        this.Address = Address;
    }

    public String getAddress() 
    {
        return Address;
    }

    public void setCommunity(String Community) 
    {
        this.Community = Community;
    }

    public String getCommunity() 
    {
        return Community;
    }

    public void setAccessibility(String Accessibility) 
    {
        this.Accessibility = Accessibility;
    }

    public String getAccessibility() 
    {
        return Accessibility;
    }

    public void setAvailableHeight(Long AvailableHeight) 
    {
        this.AvailableHeight = AvailableHeight;
    }

    public Long getAvailableHeight() 
    {
        return AvailableHeight;
    }

    public void setAvailableLength(Long AvailableLength) 
    {
        this.AvailableLength = AvailableLength;
    }

    public Long getAvailableLength() 
    {
        return AvailableLength;
    }

    public void setAvailableWidth(Long AvailableWidth) 
    {
        this.AvailableWidth = AvailableWidth;
    }

    public Long getAvailableWidth() 
    {
        return AvailableWidth;
    }

    public void setConstructionMaterial(String ConstructionMaterial) 
    {
        this.ConstructionMaterial = ConstructionMaterial;
    }

    public String getConstructionMaterial() 
    {
        return ConstructionMaterial;
    }

    public void setForeignCode(String ForeignCode) 
    {
        this.ForeignCode = ForeignCode;
    }

    public String getForeignCode() 
    {
        return ForeignCode;
    }

    public void setLength(Long Length) 
    {
        this.Length = Length;
    }

    public Long getLength() 
    {
        return Length;
    }

    public void setMaximalShipLength(Long MaximalShipLength) 
    {
        this.MaximalShipLength = MaximalShipLength;
    }

    public Long getMaximalShipLength() 
    {
        return MaximalShipLength;
    }

    public void setMaximumConvoyHeight(Long MaximumConvoyHeight) 
    {
        this.MaximumConvoyHeight = MaximumConvoyHeight;
    }

    public Long getMaximumConvoyHeight() 
    {
        return MaximumConvoyHeight;
    }

    public void setMaximumConvoyLength(Long MaximumConvoyLength) 
    {
        this.MaximumConvoyLength = MaximumConvoyLength;
    }

    public Long getMaximumConvoyLength() 
    {
        return MaximumConvoyLength;
    }

    public void setMaximumConvoyWidth(Long MaximumConvoyWidth) 
    {
        this.MaximumConvoyWidth = MaximumConvoyWidth;
    }

    public Long getMaximumConvoyWidth() 
    {
        return MaximumConvoyWidth;
    }

    public void setMaximumDraught(Long MaximumDraught) 
    {
        this.MaximumDraught = MaximumDraught;
    }

    public Long getMaximumDraught() 
    {
        return MaximumDraught;
    }

    public void setMaximumShipHeight(Long MaximumShipHeight) 
    {
        this.MaximumShipHeight = MaximumShipHeight;
    }

    public Long getMaximumShipHeight() 
    {
        return MaximumShipHeight;
    }

    public void setMaximumShipWidth(Long MaximumShipWidth) 
    {
        this.MaximumShipWidth = MaximumShipWidth;
    }

    public Long getMaximumShipWidth() 
    {
        return MaximumShipWidth;
    }

    public void setMaximumStayDurationH(Long MaximumStayDurationH) 
    {
        this.MaximumStayDurationH = MaximumStayDurationH;
    }

    public Long getMaximumStayDurationH() 
    {
        return MaximumStayDurationH;
    }

    public void setMinimalShipLength(Long MinimalShipLength) 
    {
        this.MinimalShipLength = MinimalShipLength;
    }

    public Long getMinimalShipLength() 
    {
        return MinimalShipLength;
    }

    public void setMooringFacilitiesDesc(String MooringFacilitiesDesc) 
    {
        this.MooringFacilitiesDesc = MooringFacilitiesDesc;
    }

    public String getMooringFacilitiesDesc() 
    {
        return MooringFacilitiesDesc;
    }

    public void setNumberOfMooringFacilities(Long NumberOfMooringFacilities) 
    {
        this.NumberOfMooringFacilities = NumberOfMooringFacilities;
    }

    public Long getNumberOfMooringFacilities() 
    {
        return NumberOfMooringFacilities;
    }

    public void setNumberOfRows(Long NumberOfRows) 
    {
        this.NumberOfRows = NumberOfRows;
    }

    public Long getNumberOfRows() 
    {
        return NumberOfRows;
    }

    public void setPortAreaId(Long PortAreaId) 
    {
        this.PortAreaId = PortAreaId;
    }

    public Long getPortAreaId() 
    {
        return PortAreaId;
    }

    public void setQuayQuality(String QuayQuality) 
    {
        this.QuayQuality = QuayQuality;
    }

    public String getQuayQuality() 
    {
        return QuayQuality;
    }

    public void setRotation(Long Rotation) 
    {
        this.Rotation = Rotation;
    }

    public Long getRotation() 
    {
        return Rotation;
    }

    public void setShorelineCategory(String ShorelineCategory) 
    {
        this.ShorelineCategory = ShorelineCategory;
    }

    public String getShorelineCategory() 
    {
        return ShorelineCategory;
    }

    public void setSignaling(String Signaling) 
    {
        this.Signaling = Signaling;
    }

    public String getSignaling() 
    {
        return Signaling;
    }

    public void setSignalling(String Signalling) 
    {
        this.Signalling = Signalling;
    }

    public String getSignalling() 
    {
        return Signalling;
    }

    public void setTransportPossibilitiesDesc(String TransportPossibilitiesDesc) 
    {
        this.TransportPossibilitiesDesc = TransportPossibilitiesDesc;
    }

    public String getTransportPossibilitiesDesc() 
    {
        return TransportPossibilitiesDesc;
    }

    public void setUsageConditions(String UsageConditions) 
    {
        this.UsageConditions = UsageConditions;
    }

    public String getUsageConditions() 
    {
        return UsageConditions;
    }

    public void setWidth(Long Width) 
    {
        this.Width = Width;
    }

    public Long getWidth() 
    {
        return Width;
    }

    public void setFacility(String Facility) 
    {
        this.Facility = Facility;
    }

    public String getFacility() 
    {
        return Facility;
    }

    public void setShipCategories(String ShipCategories) 
    {
        this.ShipCategories = ShipCategories;
    }

    public String getShipCategories() 
    {
        return ShipCategories;
    }

    public void setStatus(String Status) 
    {
        this.Status = Status;
    }

    public String getStatus() 
    {
        return Status;
    }

    public void setTransshipmentGoods(String TransshipmentGoods) 
    {
        this.TransshipmentGoods = TransshipmentGoods;
    }

    public String getTransshipmentGoods() 
    {
        return TransshipmentGoods;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("Id", getId())
            .append("GeoType", getGeoType())
            .append("GeoGeneration", getGeoGeneration())
            .append("GeoJSON", getGeoJSON())
            .append("Name", getName())
            .append("Geometry", getGeometry())
            .append("VinCode", getVinCode())
            .append("UnLocationCode", getUnLocationCode())
            .append("Note", getNote())
            .append("RouteId", getRouteId())
            .append("RouteKmBegin", getRouteKmBegin())
            .append("RouteKmEnd", getRouteKmEnd())
            .append("City", getCity())
            .append("AdministrationId", getAdministrationId())
            .append("FairwaySectionId", getFairwaySectionId())
            .append("FairwayId", getFairwayId())
            .append("IsrsId", getIsrsId())
            .append("Condition", getCondition())
            .append("Category", getCategory())
            .append("WidthPosition", getWidthPosition())
            .append("HasCrane", getHasCrane())
            .append("TerminalId", getTerminalId())
            .append("Address", getAddress())
            .append("Community", getCommunity())
            .append("Accessibility", getAccessibility())
            .append("AvailableHeight", getAvailableHeight())
            .append("AvailableLength", getAvailableLength())
            .append("AvailableWidth", getAvailableWidth())
            .append("ConstructionMaterial", getConstructionMaterial())
            .append("ForeignCode", getForeignCode())
            .append("Length", getLength())
            .append("MaximalShipLength", getMaximalShipLength())
            .append("MaximumConvoyHeight", getMaximumConvoyHeight())
            .append("MaximumConvoyLength", getMaximumConvoyLength())
            .append("MaximumConvoyWidth", getMaximumConvoyWidth())
            .append("MaximumDraught", getMaximumDraught())
            .append("MaximumShipHeight", getMaximumShipHeight())
            .append("MaximumShipWidth", getMaximumShipWidth())
            .append("MaximumStayDurationH", getMaximumStayDurationH())
            .append("MinimalShipLength", getMinimalShipLength())
            .append("MooringFacilitiesDesc", getMooringFacilitiesDesc())
            .append("NumberOfMooringFacilities", getNumberOfMooringFacilities())
            .append("NumberOfRows", getNumberOfRows())
            .append("PortAreaId", getPortAreaId())
            .append("QuayQuality", getQuayQuality())
            .append("Rotation", getRotation())
            .append("ShorelineCategory", getShorelineCategory())
            .append("Signaling", getSignaling())
            .append("Signalling", getSignalling())
            .append("TransportPossibilitiesDesc", getTransportPossibilitiesDesc())
            .append("UsageConditions", getUsageConditions())
            .append("Width", getWidth())
            .append("Facility", getFacility())
            .append("ShipCategories", getShipCategories())
            .append("Status", getStatus())
            .append("TransshipmentGoods", getTransshipmentGoods())
            .toString();
    }
}
