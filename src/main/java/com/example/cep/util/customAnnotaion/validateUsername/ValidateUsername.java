package com.example.cep.util.customAnnotaion.validateUsername;

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
@Constraint(validatedBy = UsernameValidator.class)
@Documented
public @interface ValidateUsername {
  String message() default "영어 대소문자와 숫자를 포함하여 4자 이상 12자 이하";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };
}
