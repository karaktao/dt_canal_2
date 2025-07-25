package com.ruoyi.infra.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * VNDSlock对象 rws_VNDS_lock
 * 
 * @author dt
 * @date 2025-07-25
 */
public class RwsVndsLock extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 唯一标识符，船闸 ID */
    private Long Id;

    /** 数据生成版本号 */
    @Excel(name = "数据生成版本号")
    private Long GeoGeneration;

    /** 地理对象类型，如 lock */
    @Excel(name = "地理对象类型，如 lock")
    private String GeoType;

    /** 几何信息，WKT 格式坐标 */
    @Excel(name = "几何信息，WKT 格式坐标")
    private String Geometry;

    /** 船闸名称 */
    @Excel(name = "船闸名称")
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

    /** 航线起点公里数 */
    @Excel(name = "航线起点公里数")
    private Long RouteKmBegin;

    /** 航线终点公里数 */
    @Excel(name = "航线终点公里数")
    private Long RouteKmEnd;

    /** 是否为航行计划参考点 */
    @Excel(name = "是否为航行计划参考点")
    private Integer VoyagePlanningPoint;

    /** 地址信息 */
    @Excel(name = "地址信息")
    private String Address;

    /** 管理机构 ID */
    @Excel(name = "管理机构 ID")
    private Long AdministrationId;

    /** 所属城市 */
    @Excel(name = "所属城市")
    private String City;

    /** 当前状态，如建成(CONSTRUCTED) */
    @Excel(name = "当前状态，如建成(CONSTRUCTED)")
    private String Condition;

    /** 外部代码/识别符 */
    @Excel(name = "外部代码/识别符")
    private String ForeignCode;

    /** 是否远程控制 */
    @Excel(name = "是否远程控制")
    private Integer IsRemoteControlled;

    /** 国际航道识别码 ISRS */
    @Excel(name = "国际航道识别码 ISRS")
    private Long IsrsId;

    /** 总长度（米） */
    @Excel(name = "总长度", readConverterExp = "米=")
    private Long Length;

    /** 最大水位升高（米） */
    @Excel(name = "最大水位升高", readConverterExp = "米=")
    private Long MaximumRise;

    /** 备注信息 */
    @Excel(name = "备注信息")
    private String Note;

    /** 闸室数量 */
    @Excel(name = "闸室数量")
    private Long NumberOfChambers;

    /** 操作时间段 ID */
    @Excel(name = "操作时间段 ID")
    private Long OperatingTimesId;

    /** 联系或操作电话 */
    @Excel(name = "联系或操作电话")
    private String PhoneNumber;

    /** 邮政编码 */
    @Excel(name = "邮政编码")
    private String PostalCode;

    /** 参考水位上界（BeBu） */
    @Excel(name = "参考水位上界", readConverterExp = "B=eBu")
    private String ReferenceLevelBeBu;

    /** 参考水位下界（BoBi） */
    @Excel(name = "参考水位下界", readConverterExp = "B=oBi")
    private String ReferenceLevelBoBi;

    /** 相关建筑名称 */
    @Excel(name = "相关建筑名称")
    private String RelatedBuildingComplexName;

    /** 地图旋转角度（度） */
    @Excel(name = "地图旋转角度", readConverterExp = "度=")
    private Long Rotation;

    /** 锁闸 VIN 编码 */
    @Excel(name = "锁闸 VIN 编码")
    private String VinCode;

    /** 宽度（米） */
    @Excel(name = "宽度", readConverterExp = "米=")
    private Long Width;

    /** 记录创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "记录创建时间", width = 30, dateFormat = "yyyy-MM-dd")
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

    public void setVoyagePlanningPoint(Integer VoyagePlanningPoint) 
    {
        this.VoyagePlanningPoint = VoyagePlanningPoint;
    }

    public Integer getVoyagePlanningPoint() 
    {
        return VoyagePlanningPoint;
    }

    public void setAddress(String Address) 
    {
        this.Address = Address;
    }

    public String getAddress() 
    {
        return Address;
    }

    public void setAdministrationId(Long AdministrationId) 
    {
        this.AdministrationId = AdministrationId;
    }

    public Long getAdministrationId() 
    {
        return AdministrationId;
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

    public void setMaximumRise(Long MaximumRise) 
    {
        this.MaximumRise = MaximumRise;
    }

    public Long getMaximumRise() 
    {
        return MaximumRise;
    }

    public void setNote(String Note) 
    {
        this.Note = Note;
    }

    public String getNote() 
    {
        return Note;
    }

    public void setNumberOfChambers(Long NumberOfChambers) 
    {
        this.NumberOfChambers = NumberOfChambers;
    }

    public Long getNumberOfChambers() 
    {
        return NumberOfChambers;
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

    public void setPostalCode(String PostalCode) 
    {
        this.PostalCode = PostalCode;
    }

    public String getPostalCode() 
    {
        return PostalCode;
    }

    public void setReferenceLevelBeBu(String ReferenceLevelBeBu) 
    {
        this.ReferenceLevelBeBu = ReferenceLevelBeBu;
    }

    public String getReferenceLevelBeBu() 
    {
        return ReferenceLevelBeBu;
    }

    public void setReferenceLevelBoBi(String ReferenceLevelBoBi) 
    {
        this.ReferenceLevelBoBi = ReferenceLevelBoBi;
    }

    public String getReferenceLevelBoBi() 
    {
        return ReferenceLevelBoBi;
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
            .append("VoyagePlanningPoint", getVoyagePlanningPoint())
            .append("Address", getAddress())
            .append("AdministrationId", getAdministrationId())
            .append("City", getCity())
            .append("Condition", getCondition())
            .append("ForeignCode", getForeignCode())
            .append("IsRemoteControlled", getIsRemoteControlled())
            .append("IsrsId", getIsrsId())
            .append("Length", getLength())
            .append("MaximumRise", getMaximumRise())
            .append("Note", getNote())
            .append("NumberOfChambers", getNumberOfChambers())
            .append("OperatingTimesId", getOperatingTimesId())
            .append("PhoneNumber", getPhoneNumber())
            .append("PostalCode", getPostalCode())
            .append("ReferenceLevelBeBu", getReferenceLevelBeBu())
            .append("ReferenceLevelBoBi", getReferenceLevelBoBi())
            .append("RelatedBuildingComplexName", getRelatedBuildingComplexName())
            .append("Rotation", getRotation())
            .append("VinCode", getVinCode())
            .append("Width", getWidth())
            .append("CreatedAt", getCreatedAt())
            .append("GeoJSON", getGeoJSON())
            .toString();
    }
}
