package com.example.cep.util.customAnnotaion.validateEmail;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidateEmail, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null)
      return false;
    Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    Matcher matcher = pattern.matcher(value);
    return matcher.matches();
  }
}
