package com.woodenfurniture.validation.validator;

import com.woodenfurniture.validation.annotation.EmailFormat;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<EmailFormat, String> {
    private final Pattern emailPattern = Pattern.compile("^\\S+@\\S+.\\S+$");

    @Override
    public void initialize(EmailFormat emailFormat) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext cxt) {
        if (StringUtils.hasText(value)) {
            Matcher matcher = emailPattern.matcher(value);
            return matcher.matches();
        }
        // Check empty string
        return !Objects.nonNull(value) || !value.isBlank();
    }
}
