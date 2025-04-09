package com.woodenfurniture.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chart", schema = "dac_dashboard")
public class Chart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String code;
    private String description;
    
    @Column(name = "title_highlight")
    private String titleHighlight;
    
    @Column(name = "title_icon")
    private String titleIcon;
    
    @Column(name = "title_text")
    private String titleText;
    
    @Column(name = "title_sub_text")
    private String titleSubText;
    
    @Column(name = "title_left")
    private Integer titleLeft;
    
    @Column(name = "title_top")
    private Integer titleTop;
    
    @Column(name = "grid_top")
    private Integer gridTop;
    
    @Column(name = "grid_left")
    private Integer gridLeft;
    
    @Column(name = "grid_bottom")
    private Integer gridBottom;
    
    @Column(name = "grid_right")
    private Integer gridRight;
    
    @Column(name = "is_show_title")
    private Boolean isShowTitle;
    
    @Column(name = "sql_command")
    private String sqlCommand;
    
    @Column(name = "is_show_legend")
    private Boolean isShowLegend;
    
    @Column(name = "legend_orient")
    private String legendOrient;
    
    @Column(name = "legend_item_gap")
    private Integer legendItemGap;
    
    @Column(name = "legend_left")
    private Integer legendLeft;
    
    @Column(name = "legend_top")
    private Integer legendTop;
    
    @Column(name = "empty_option")
    private String emptyOption;
    
    @Column(name = "border_top")
    private Integer borderTop;
    
    @Column(name = "border_left")
    private Integer borderLeft;
    
    @Column(name = "border_right")
    private Integer borderRight;
    
    @Column(name = "border_bottom")
    private Integer borderBottom;
    
    @Column(name = "border_radius")
    private Integer borderRadius;
    
    @Column(name = "rows_cell")
    private Integer rowsCell;
    
    @Column(name = "scroll_number")
    private Integer scrollNumber;
    
    @Column(name = "cols_cell")
    private Integer colsCell;
    
    @Column(name = "title_style_id")
    private Long titleStyleId;
    
    @Column(name = "legend_style_id")
    private Long legendStyleId;
    
    @Column(name = "type_id")
    private Long typeId;
    
    @Column(name = "tool_tip_info")
    private String toolTipInfo;
    
    @Column(name = "tool_tip_sql")
    private String toolTipSql;
} 