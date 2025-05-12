package com.woodenfurniture.province;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "d_province_name_vi")
public class Province implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "province_code", length = 10)
    private String provinceCode;

    @Column(name = "province_key", length = 50)
    private String provinceKey;

    @Column(name = "province_name", length = 50)
    private String provinceName;

    @Column(name = "province_code_number", length = 255)
    private String provinceCodeNumber;

    @Column(name = "area_code", length = 10)
    private String areaCode;
}
