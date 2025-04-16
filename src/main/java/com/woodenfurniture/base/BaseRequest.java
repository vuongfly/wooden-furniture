package com.woodenfurniture.base;

import lombok.Getter;
import lombok.Setter;

/**
 * Base class for all request DTOs
 *
 * @param <T> The entity type this request maps to
 */
@Getter
@Setter
public abstract class BaseRequest<T extends BaseEntity> {
} 