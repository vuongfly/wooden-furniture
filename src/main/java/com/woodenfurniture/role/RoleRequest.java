package com.woodenfurniture.role;

import com.woodenfurniture.base.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest extends BaseRequest<Role> {
    @NotBlank(message = "Role name is required")
    String name;
    
    String description;
    
    Set<String> permissionNames;
} 