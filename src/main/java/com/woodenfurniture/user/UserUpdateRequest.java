package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseRequest;
import com.woodenfurniture.enums.Gender;
import com.woodenfurniture.validation.annotation.DobConstraint;
import com.woodenfurniture.validation.annotation.EmailFormat;
import com.woodenfurniture.validation.annotation.PhoneFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class UserUpdateRequest extends BaseRequest<User> {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @EmailFormat
    @Size(max = 50)
    private String email;
    
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    private String fullName;
    
    @NumberFormat
    Integer age;
    Gender gender;
    @PhoneFormat
    @Size(max = 50)
    String phoneNumber;
    List<String> roles;
    @DobConstraint(message = "INVALID_DOB", min = 18)
    LocalDate dob;

}
