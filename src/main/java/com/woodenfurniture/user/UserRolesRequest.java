package com.woodenfurniture.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request for adding or removing roles from a user
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRolesRequest {
    @NotEmpty(message = "Role names are required")
    private List<String> roleNames;
}