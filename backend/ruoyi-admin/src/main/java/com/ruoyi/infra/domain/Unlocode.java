package com.ruoyi.infra.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * UNLOCODE对象 nl_unlocode
 * 
 * @author dt
 * @date 2025-06-28
 */
public class Unlocode extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 荷兰文: LOCODE; 中文: UN/LOCODE（国家码+地点码） */
    @Excel(name = "荷兰文: LOCODE; 中文: UN/LOCODE", readConverterExp = "国=家码+地点码")
    private String locode;

    /** 荷兰文: Name; 中文: 地点名称 */
    @Excel(name = "荷兰文: Name; 中文: 地点名称")
    private String name;

    /** 荷兰文: NameWoDiacritics; 中文: 无重音符号地名 */
    @Excel(name = "荷兰文: NameWoDiacritics; 中文: 无重音符号地名")
    private String nameWoDiacritics;

    public void setLocode(String locode) 
    {
        this.locode = locode;
    }

    public String getLocode() 
    {
        return locode;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setNameWoDiacritics(String nameWoDiacritics) 
    {
        this.nameWoDiacritics = nameWoDiacritics;
    }

    public String getNameWoDiacritics() 
    {
        return nameWoDiacritics;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("locode", getLocode())
            .append("name", getName())
            .append("nameWoDiacritics", getNameWoDiacritics())
            .toString();
    }
}
