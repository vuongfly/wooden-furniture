package com.woodenfurniture.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChartRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Code is required")
    private String code;
    
    private String description;
    private String titleHighlight;
    private String titleIcon;
    private String titleText;
    private String titleSubText;
    private Integer titleLeft;
    private Integer titleTop;
    private Integer gridTop;
    private Integer gridLeft;
    private Integer gridBottom;
    private Integer gridRight;
    private Boolean isShowTitle;
    private String sqlCommand;
    private Boolean isShowLegend;
    private String legendOrient;
    private Integer legendItemGap;
    private Integer legendLeft;
    private Integer legendTop;
    private String emptyOption;
    private Integer borderTop;
    private Integer borderLeft;
    private Integer borderRight;
    private Integer borderBottom;
    private Integer borderRadius;
    private Integer rowsCell;
    private Integer scrollNumber;
    private Integer colsCell;
    private Long titleStyleId;
    private Long legendStyleId;
    private Long typeId;
    private String toolTipInfo;
    private String toolTipSql;
} 