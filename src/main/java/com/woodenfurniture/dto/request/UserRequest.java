package com.woodenfurniture.dto.request;

import com.woodenfurniture.common.Address;
import com.woodenfurniture.common.Gender;
import com.woodenfurniture.validation.annotation.EmailFormat;
import com.woodenfurniture.validation.annotation.PhoneFormat;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.Size;

@Data
public class UserRequest {
    private String name;
    private Gender gender;
    private Address address;
    @NumberFormat
    private Integer age;
    private String username;
    private String password;
    @EmailFormat
    @Size(max = 50)
    private String email;
    @PhoneFormat
    @Size(max = 50)
    private String phoneNumber;

}
