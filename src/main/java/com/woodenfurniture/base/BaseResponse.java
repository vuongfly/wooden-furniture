package com.woodenfurniture.base;

import lombok.Getter;
import lombok.Setter;

/**
 * Base class for all response DTOs
 * @param <T> The entity type this response maps from
 */
@Getter
@Setter
public abstract class BaseResponse<T extends BaseEntity> {
} 