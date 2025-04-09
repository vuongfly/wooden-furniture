package com.woodenfurniture.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChartSearchRequest {
    private String name;
    private String code;
    private String description;
    private Boolean isShowTitle;
    private Boolean isShowLegend;
    private Long typeId;
} 