package com.woodenfurniture.base;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Base class for all response DTOs
 *
 * @param <T> The entity type this response maps from
 */
@Getter
@Setter
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class BaseResponse<T extends BaseEntity> {
} 