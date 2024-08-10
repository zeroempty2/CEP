package com.example.cep.util.customAnnotaion.validatePassword;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD,PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface ValidatePassword {
  String message() default "소문자와 대문자 특수문자 그리고 숫자를 포함하여 8자 이상 15자 이하";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };
}
