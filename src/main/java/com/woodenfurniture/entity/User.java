package com.woodenfurniture.entity;

import com.woodenfurniture.common.Gender;
import com.woodenfurniture.validation.annotation.EmailFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;

@Data
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity
public class User {
    @Id
    String id;
    String name;
    @Enumerated(EnumType.STRING)
    Gender gender;
//    Address address;
    Integer age;
    @Size(min = 3, message = "USERNAME_INVALID")
    String username;
    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;
    @EmailFormat
    String email;
    String phoneNumber;
}
