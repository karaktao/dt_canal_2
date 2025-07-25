package com.ruoyi.infra.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * MunicipalityReferenceTable对象 nl_municipality_reference
 * 
 * @author dt
 * @date 2025-06-28
 */
public class MunicipalityReference extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 荷兰文: Gemeentecode; 中文: 市政代码 */
    @Excel(name = "荷兰文: Gemeentecode; 中文: 市政代码")
    private String municipalityCode;

    /** 荷兰文: GemeentecodeGM; 中文: 市政代码GM */
    @Excel(name = "荷兰文: GemeentecodeGM; 中文: 市政代码GM")
    private String municipalityCodeGm;

    /** 荷兰文: Gemeentenaam; 中文: 市政名称 */
    @Excel(name = "荷兰文: Gemeentenaam; 中文: 市政名称")
    private String municipalityName;

    /** 荷兰文: Provinciecode; 中文: 省代码 */
    @Excel(name = "荷兰文: Provinciecode; 中文: 省代码")
    private String provinceCode;

    /** 荷兰文: ProvinciecodePV; 中文: 省代码PV */
    @Excel(name = "荷兰文: ProvinciecodePV; 中文: 省代码PV")
    private String provinceCodePv;

    /** 荷兰文: Provincienaam; 中文: 省名称 */
    @Excel(name = "荷兰文: Provincienaam; 中文: 省名称")
    private String provinceName;

    public void setMunicipalityCode(String municipalityCode) 
    {
        this.municipalityCode = municipalityCode;
    }

    public String getMunicipalityCode() 
    {
        return municipalityCode;
    }

    public void setMunicipalityCodeGm(String municipalityCodeGm) 
    {
        this.municipalityCodeGm = municipalityCodeGm;
    }

    public String getMunicipalityCodeGm() 
    {
        return municipalityCodeGm;
    }

    public void setMunicipalityName(String municipalityName) 
    {
        this.municipalityName = municipalityName;
    }

    public String getMunicipalityName() 
    {
        return municipalityName;
    }

    public void setProvinceCode(String provinceCode) 
    {
        this.provinceCode = provinceCode;
    }

    public String getProvinceCode() 
    {
        return provinceCode;
    }

    public void setProvinceCodePv(String provinceCodePv) 
    {
        this.provinceCodePv = provinceCodePv;
    }

    public String getProvinceCodePv() 
    {
        return provinceCodePv;
    }

    public void setProvinceName(String provinceName) 
    {
        this.provinceName = provinceName;
    }

    public String getProvinceName() 
    {
        return provinceName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("municipalityCode", getMunicipalityCode())
            .append("municipalityCodeGm", getMunicipalityCodeGm())
            .append("municipalityName", getMunicipalityName())
            .append("provinceCode", getProvinceCode())
            .append("provinceCodePv", getProvinceCodePv())
            .append("provinceName", getProvinceName())
            .toString();
    }
}
