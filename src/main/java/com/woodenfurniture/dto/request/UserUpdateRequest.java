package com.woodenfurniture.dto.request;

import com.woodenfurniture.enums.Gender;
import com.woodenfurniture.validation.annotation.DobConstraint;
import com.woodenfurniture.validation.annotation.EmailFormat;
import com.woodenfurniture.validation.annotation.PhoneFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String name;
    Gender gender;
    @NumberFormat
    Integer age;
    @Size(min = 3, message = "USERNAME_INVALID")
//    String username;
//    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;
    @EmailFormat
    @Size(max = 50)
    String email;
    @PhoneFormat
    @Size(max = 50)
    String phoneNumber;
    List<String> roles;
    @DobConstraint(message = "INVALID_DOB", min = 18)
    LocalDate dob;
}
