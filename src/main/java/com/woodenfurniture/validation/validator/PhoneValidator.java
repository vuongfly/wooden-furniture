
package com.woodenfurniture.validation.validator;

import com.woodenfurniture.validation.annotation.PhoneFormat;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<PhoneFormat, String> {
    private Pattern phonePattern = Pattern.compile("^\\d{9,12}$");

    @Override
    public void initialize(PhoneFormat phoneFormat) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext cxt) {
        if (StringUtils.hasText(value)) {
            Matcher matcher = phonePattern.matcher(value);
            return matcher.matches();
        }
        return true;
    }
}
