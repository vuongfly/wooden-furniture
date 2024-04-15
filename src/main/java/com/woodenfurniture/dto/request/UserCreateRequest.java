package com.woodenfurniture.dto.request;

import com.woodenfurniture.common.Address;
import com.woodenfurniture.common.Gender;
import com.woodenfurniture.validation.annotation.EmailFormat;
import com.woodenfurniture.validation.annotation.PhoneFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.Size;

@Data
@FieldDefaults(level = AccessLevel.PACKAGE)
public class UserCreateRequest {
    String name;
    Gender gender;
//    Address address;
    @NumberFormat
    Integer age;
    String username;
    @Size(min = 8, message = "password must be at least 8 characters")
    String password;
    @EmailFormat
    @Size(max = 50)
    String email;
    @PhoneFormat
    @Size(max = 50)
    String phoneNumber;
}
