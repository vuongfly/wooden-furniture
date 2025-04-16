package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseRequest;
import com.woodenfurniture.enums.Gender;
import com.woodenfurniture.validation.annotation.DobConstraint;
import com.woodenfurniture.validation.annotation.PhoneFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.NumberFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest extends BaseRequest<User> {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    Gender gender;
    @NumberFormat
    Integer age;
    @PhoneFormat
    @Size(max = 50)
    String phoneNumber;
    List<String> roles;
    @DobConstraint(message = "INVALID_DOB", min = 18)
    LocalDate dob;
}
