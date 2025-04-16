package com.woodenfurniture.permission;

import com.woodenfurniture.base.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest extends BaseRequest<Permission> {
    @NotBlank(message = "Permission name is required")
    String name;
    
    String description;

} 