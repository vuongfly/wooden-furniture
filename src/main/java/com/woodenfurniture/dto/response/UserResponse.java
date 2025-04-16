package com.woodenfurniture.dto.response;

import com.woodenfurniture.base.BaseDTO;
import com.woodenfurniture.enums.Gender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse extends BaseDTO {
    String name;
    Gender gender;
    Integer age;
    String username;
    String email;
    String phoneNumber;
    Set<RoleResponse> roles;
    LocalDate dob;
}
