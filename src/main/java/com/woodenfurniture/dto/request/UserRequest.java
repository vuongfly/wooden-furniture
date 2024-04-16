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
@FieldDefaults(level= AccessLevel.PRIVATE)
public class UserRequest {
    String name;
    Gender gender;
    Address address;
    @NumberFormat
    Integer age;
    @Size(min = 3, message = "USERNAME_INVALID")
    String username;
    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;
    @EmailFormat
    @Size(max = 50)
    String email;
    @PhoneFormat
    @Size(max = 50)
    String phoneNumber;

}
