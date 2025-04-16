package com.woodenfurniture.authentication;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
//    @Size(min = 3, message = "USERNAME_INVALID")
    String username;
//    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;

}
