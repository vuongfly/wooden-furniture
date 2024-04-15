package com.woodenfurniture.common;

import lombok.Data;

@Data
public class BaseEntity {
    private Long id;
    private String uuid;
    private Data createTime;
    private Data updateTime;
}
