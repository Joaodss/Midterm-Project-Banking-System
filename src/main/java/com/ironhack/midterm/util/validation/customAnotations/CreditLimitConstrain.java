package com.ironhack.midterm.util.validation.customAnotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {CreditLimitValidation.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CreditLimitConstrain {

  String message() default "Invalid Credit limit";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
