package com.example.cep.util.customAnnotaion.validatePassword;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidatePassword, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if(value == null) return false;
    Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$");
    Matcher matcher = pattern.matcher(value);
    return matcher.matches();
  }
}
