package com.woodenfurniture.dto.response;

import com.woodenfurniture.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
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
    Set<String> roles;
}
