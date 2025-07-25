package com.ruoyi.infra.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * VNDSbridge对象 rws_VNDS_bridge
 * 
 * @author dt
 * @date 2025-07-25
 */
public class RwsVndsBridge extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一标识符，桥梁 ID */
    private Long Id;

    /** 数据版本号 */
    @Excel(name = "数据版本号")
    private Long GeoGeneration;

    /** 地理对象类型，如 bridge */
    @Excel(name = "地理对象类型，如 bridge")
    private String GeoType;

    /** 几何信息，WKT 格式坐标 */
    @Excel(name = "几何信息，WKT 格式坐标")
    private String Geometry;

    /** 桥梁名称 */
    @Excel(name = "桥梁名称")
    private String Name;

    /** 所属航道 ID */
    @Excel(name = "所属航道 ID")
    private Long FairwayId;

    /** 所属航道区段 ID */
    @Excel(name = "所属航道区段 ID")
    private Long FairwaySectionId;

    /** 所属航线 ID */
    @Excel(name = "所属航线 ID")
    private Long RouteId;

    /** 起点公里数 */
    @Excel(name = "起点公里数")
    private Long RouteKmBegin;

    /** 终点公里数 */
    @Excel(name = "终点公里数")
    private Long RouteKmEnd;

    /** 高程参考系统，如 KP */
    @Excel(name = "高程参考系统，如 KP")
    private String Referencelevel;

    /** 高水位参考层，如 CANAL */
    @Excel(name = "高水位参考层，如 CANAL")
    private String MhwReferenceLevel;

    /** 是否为航行计划参考点 */
    @Excel(name = "是否为航行计划参考点")
    private Integer VoyagePlanningPoint;

    /** 管理机构 ID */
    @Excel(name = "管理机构 ID")
    private Long AdministrationId;

    /** 是否为可开启桥 */
    @Excel(name = "是否为可开启桥")
    private Integer CanOpen;

    /** 所在城市 */
    @Excel(name = "所在城市")
    private String City;

    /** 桥梁状态，如建成(CONSTRUCTED) */
    @Excel(name = "桥梁状态，如建成(CONSTRUCTED)")
    private String Condition;

    /** 外部系统识别码 */
    @Excel(name = "外部系统识别码")
    private String ForeignCode;

    /** 是否为其他航道上的开启桥 */
    @Excel(name = "是否为其他航道上的开启桥")
    private Integer HasOpeningOnOtherFairway;

    /** 是否远程控制 */
    @Excel(name = "是否远程控制")
    private Integer IsRemoteControlled;

    /** 国际航道识别码 ISRS */
    @Excel(name = "国际航道识别码 ISRS")
    private Long IsrsId;

    /** 桥梁总长度（米） */
    @Excel(name = "桥梁总长度", readConverterExp = "米=")
    private Long Length;

    /** 高水位的偏移（米） */
    @Excel(name = "高水位的偏移", readConverterExp = "米=")
    private Long MhwOffset;

    /** 备注信息 */
    @Excel(name = "备注信息")
    private String Note;

    /** 可开启段数量 */
    @Excel(name = "可开启段数量")
    private Long NumberOfOpenings;

    /** 开启/控制时间段 ID */
    @Excel(name = "开启/控制时间段 ID")
    private Long OperatingTimesId;

    /** 联系或控制电话 */
    @Excel(name = "联系或控制电话")
    private String PhoneNumber;

    /** 相关建筑/船闸名称 */
    @Excel(name = "相关建筑/船闸名称")
    private String RelatedBuildingComplexName;

    /** 地图上的旋转角度（单位：度） */
    @Excel(name = "地图上的旋转角度", readConverterExp = "单=位：度")
    private Long Rotation;

    /** 桥梁的 VIN 编号 */
    @Excel(name = "桥梁的 VIN 编号")
    private String VinCode;

    /** 桥宽（米） */
    @Excel(name = "桥宽", readConverterExp = "米=")
    private Long Width;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date CreatedAt;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String GeoJSON;

    public void setId(Long Id) 
    {
        this.Id = Id;
    }

    public Long getId() 
    {
        return Id;
    }

    public void setGeoGeneration(Long GeoGeneration) 
    {
        this.GeoGeneration = GeoGeneration;
    }

    public Long getGeoGeneration() 
    {
        return GeoGeneration;
    }

    public void setGeoType(String GeoType) 
    {
        this.GeoType = GeoType;
    }

    public String getGeoType() 
    {
        return GeoType;
    }

    public void setGeometry(String Geometry) 
    {
        this.Geometry = Geometry;
    }

    public String getGeometry() 
    {
        return Geometry;
    }

    public void setName(String Name) 
    {
        this.Name = Name;
    }

    public String getName() 
    {
        return Name;
    }

    public void setFairwayId(Long FairwayId) 
    {
        this.FairwayId = FairwayId;
    }

    public Long getFairwayId() 
    {
        return FairwayId;
    }

    public void setFairwaySectionId(Long FairwaySectionId) 
    {
        this.FairwaySectionId = FairwaySectionId;
    }

    public Long getFairwaySectionId() 
    {
        return FairwaySectionId;
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

    public void setReferencelevel(String Referencelevel) 
    {
        this.Referencelevel = Referencelevel;
    }

    public String getReferencelevel() 
    {
        return Referencelevel;
    }

    public void setMhwReferenceLevel(String MhwReferenceLevel) 
    {
        this.MhwReferenceLevel = MhwReferenceLevel;
    }

    public String getMhwReferenceLevel() 
    {
        return MhwReferenceLevel;
    }

    public void setVoyagePlanningPoint(Integer VoyagePlanningPoint) 
    {
        this.VoyagePlanningPoint = VoyagePlanningPoint;
    }

    public Integer getVoyagePlanningPoint() 
    {
        return VoyagePlanningPoint;
    }

    public void setAdministrationId(Long AdministrationId) 
    {
        this.AdministrationId = AdministrationId;
    }

    public Long getAdministrationId() 
    {
        return AdministrationId;
    }

    public void setCanOpen(Integer CanOpen) 
    {
        this.CanOpen = CanOpen;
    }

    public Integer getCanOpen() 
    {
        return CanOpen;
    }

    public void setCity(String City) 
    {
        this.City = City;
    }

    public String getCity() 
    {
        return City;
    }

    public void setCondition(String Condition) 
    {
        this.Condition = Condition;
    }

    public String getCondition() 
    {
        return Condition;
    }

    public void setForeignCode(String ForeignCode) 
    {
        this.ForeignCode = ForeignCode;
    }

    public String getForeignCode() 
    {
        return ForeignCode;
    }

    public void setHasOpeningOnOtherFairway(Integer HasOpeningOnOtherFairway) 
    {
        this.HasOpeningOnOtherFairway = HasOpeningOnOtherFairway;
    }

    public Integer getHasOpeningOnOtherFairway() 
    {
        return HasOpeningOnOtherFairway;
    }

    public void setIsRemoteControlled(Integer IsRemoteControlled) 
    {
        this.IsRemoteControlled = IsRemoteControlled;
    }

    public Integer getIsRemoteControlled() 
    {
        return IsRemoteControlled;
    }

    public void setIsrsId(Long IsrsId) 
    {
        this.IsrsId = IsrsId;
    }

    public Long getIsrsId() 
    {
        return IsrsId;
    }

    public void setLength(Long Length) 
    {
        this.Length = Length;
    }

    public Long getLength() 
    {
        return Length;
    }

    public void setMhwOffset(Long MhwOffset) 
    {
        this.MhwOffset = MhwOffset;
    }

    public Long getMhwOffset() 
    {
        return MhwOffset;
    }

    public void setNote(String Note) 
    {
        this.Note = Note;
    }

    public String getNote() 
    {
        return Note;
    }

    public void setNumberOfOpenings(Long NumberOfOpenings) 
    {
        this.NumberOfOpenings = NumberOfOpenings;
    }

    public Long getNumberOfOpenings() 
    {
        return NumberOfOpenings;
    }

    public void setOperatingTimesId(Long OperatingTimesId) 
    {
        this.OperatingTimesId = OperatingTimesId;
    }

    public Long getOperatingTimesId() 
    {
        return OperatingTimesId;
    }

    public void setPhoneNumber(String PhoneNumber) 
    {
        this.PhoneNumber = PhoneNumber;
    }

    public String getPhoneNumber() 
    {
        return PhoneNumber;
    }

    public void setRelatedBuildingComplexName(String RelatedBuildingComplexName) 
    {
        this.RelatedBuildingComplexName = RelatedBuildingComplexName;
    }

    public String getRelatedBuildingComplexName() 
    {
        return RelatedBuildingComplexName;
    }

    public void setRotation(Long Rotation) 
    {
        this.Rotation = Rotation;
    }

    public Long getRotation() 
    {
        return Rotation;
    }

    public void setVinCode(String VinCode) 
    {
        this.VinCode = VinCode;
    }

    public String getVinCode() 
    {
        return VinCode;
    }

    public void setWidth(Long Width) 
    {
        this.Width = Width;
    }

    public Long getWidth() 
    {
        return Width;
    }

    public void setCreatedAt(Date CreatedAt) 
    {
        this.CreatedAt = CreatedAt;
    }

    public Date getCreatedAt() 
    {
        return CreatedAt;
    }

    public void setGeoJSON(String GeoJSON) 
    {
        this.GeoJSON = GeoJSON;
    }

    public String getGeoJSON() 
    {
        return GeoJSON;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("Id", getId())
            .append("GeoGeneration", getGeoGeneration())
            .append("GeoType", getGeoType())
            .append("Geometry", getGeometry())
            .append("Name", getName())
            .append("FairwayId", getFairwayId())
            .append("FairwaySectionId", getFairwaySectionId())
            .append("RouteId", getRouteId())
            .append("RouteKmBegin", getRouteKmBegin())
            .append("RouteKmEnd", getRouteKmEnd())
            .append("Referencelevel", getReferencelevel())
            .append("MhwReferenceLevel", getMhwReferenceLevel())
            .append("VoyagePlanningPoint", getVoyagePlanningPoint())
            .append("AdministrationId", getAdministrationId())
            .append("CanOpen", getCanOpen())
            .append("City", getCity())
            .append("Condition", getCondition())
            .append("ForeignCode", getForeignCode())
            .append("HasOpeningOnOtherFairway", getHasOpeningOnOtherFairway())
            .append("IsRemoteControlled", getIsRemoteControlled())
            .append("IsrsId", getIsrsId())
            .append("Length", getLength())
            .append("MhwOffset", getMhwOffset())
            .append("Note", getNote())
            .append("NumberOfOpenings", getNumberOfOpenings())
            .append("OperatingTimesId", getOperatingTimesId())
            .append("PhoneNumber", getPhoneNumber())
            .append("RelatedBuildingComplexName", getRelatedBuildingComplexName())
            .append("Rotation", getRotation())
            .append("VinCode", getVinCode())
            .append("Width", getWidth())
            .append("CreatedAt", getCreatedAt())
            .append("GeoJSON", getGeoJSON())
            .toString();
    }
}
