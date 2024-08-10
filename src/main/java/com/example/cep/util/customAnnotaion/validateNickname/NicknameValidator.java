package com.example.cep.util.customAnnotaion.validateNickname;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NicknameValidator implements ConstraintValidator<ValidateNickname, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null)
      return false;
    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{4,12}$");
    Matcher matcher = pattern.matcher(value);
    return matcher.matches();
  }
}
