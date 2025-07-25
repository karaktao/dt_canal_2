package com.ruoyi.infra.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * rwsCatalogus对象 rws_catalogus
 *
 * @author dt
 * @date 2025-07-07
 */
@Data
@TableName("rws_catalogus")
public class RwsCatalogus extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    @TableId(value = "id")
    private Long id;

    /** Loc.Code */
    @Excel(name = "Loc.Code")
    @TableField(value = "loc_code")
    private String locCode;

    /** Loc.Naam */
    @Excel(name = "Loc.Naam")
    @TableField(value = "loc_naam")
    private String locNaam;

    /** Loc.EPSG (EPSG 编号) */
    @Excel(name = "Loc.EPSG (EPSG 编号)")
    @TableField(value = "loc_epsg")
    private String locEpsg;

    /** Loc.X (坐标) */
    @Excel(name = "Loc.X (坐标)")
    @TableField(value = "loc_x")
    private String locX;

    /** Loc.Y (坐标) */
    @Excel(name = "Loc.Y (坐标)")
    @TableField(value = "loc_y")
    private String locY;

    /** Mercator.X (坐标) */
    @Excel(name = "Mercator.X (坐标)")
    @TableField(value = "mercator_x")
    private String mercatorX;

    /** Mercator.Y (坐标) */
    @Excel(name = "Mercator.Y (坐标)")
    @TableField(value = "mercator_y")
    private String mercatorY;

    /** Meta.Parameter_Wat_Omschrijving */
    @Excel(name = "Meta.Parameter_Wat_Omschrijving")
    @TableField(value = "meta_parameter_wat_omschrijving")
    private String metaParameterWatOmschrijving;

    /** Meta.Compartiment.Code */
    @Excel(name = "Meta.Compartiment.Code")
    @TableField(value = "meta_compartiment_code")
    private String metaCompartimentCode;

    /** Meta.Compartiment.Oms */
    @Excel(name = "Meta.Compartiment.Oms")
    @TableField(value = "meta_compartiment_oms")
    private String metaCompartimentOms;

    /** Meta.Grootheid.Code */
    @Excel(name = "Meta.Grootheid.Code")
    @TableField(value = "meta_grootheid_code")
    private String metaGrootheidCode;

    /** Meta.Grootheid.Oms */
    @Excel(name = "Meta.Grootheid.Oms")
    @TableField(value = "meta_grootheid_oms")
    private String metaGrootheidOms;

    /** Meta.Parameter.Code */
    @Excel(name = "Meta.Parameter.Code")
    @TableField(value = "meta_parameter_code")
    private String metaParameterCode;

    /** Meta.Parameter.Oms */
    @Excel(name = "Meta.Parameter.Oms")
    @TableField(value = "meta_parameter_oms")
    private String metaParameterOms;

    /** Meta.Eenheid */
    @Excel(name = "Meta.Eenheid")
    @TableField(value = "meta_eenheid")
    private String metaEenheid;

    /** Meta.Hoedanigheid */
    @Excel(name = "Meta.Hoedanigheid")
    @TableField(value = "meta_hoedanigheid")
    private String metaHoedanigheid;

    /** Meta.Typering */
    @Excel(name = "Meta.Typering")
    @TableField(value = "meta_typering")
    private String metaTypering;

    /** Meta.WaardeBewerkingsmethode */
    @Excel(name = "Meta.WaardeBewerkingsmethode")
    @TableField(value = "meta_waarde_bewerkingsmethode")
    private String metaWaardeBewerkingsmethode;

    /** Meta.BioTaxon */
    @Excel(name = "Meta.BioTaxon")
    @TableField(value = "meta_biotaxon")
    private String metaBiotaxon;

    /** Meta.BioTaxon_Compartiment */
    @Excel(name = "Meta.BioTaxon_Compartiment")
    @TableField(value = "meta_biotaxon_compartiment")
    private String metaBiotaxonCompartiment;

    /** Meta.Orgaan */
    @Excel(name = "Meta.Orgaan")
    @TableField(value = "meta_orgaan")
    private String metaOrgaan;

    /** Meta.Orgaan */
    @Excel(name = "hasWaterLevel")
    @TableField(value = "has_water_level")
    private String hasWaterLevel;
}
