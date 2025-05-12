package com.woodenfurniture.province;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceResponse {
    private Long id;
    private String provinceCode;
    private String provinceKey;
    private String provinceName;
    private String provinceCodeNumber;
    private String areaCode;
}
