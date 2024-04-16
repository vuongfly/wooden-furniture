package com.woodenfurniture.dto.request;

import com.woodenfurniture.common.Address;
import com.woodenfurniture.common.Gender;
import com.woodenfurniture.validation.annotation.EmailFormat;
import com.woodenfurniture.validation.annotation.PhoneFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.Size;

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
