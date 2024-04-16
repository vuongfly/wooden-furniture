package com.woodenfurniture.dto.response;

import com.woodenfurniture.common.Gender;
import com.woodenfurniture.validation.annotation.EmailFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;

@Data
@FieldDefaults(level= AccessLevel.PRIVATE)
@Builder
public class UserResponse {
    String id;
    String name;
    Gender gender;
    //    Address address;
    Integer age;
    String username;
//    String password;
    String email;
    String phoneNumber;
}
