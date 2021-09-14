package com.ironhack.midterm.account.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {SavingsMinBalanceValidation.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SavingsMinBalanceConstrain {

    String message() default "Invalid Savings Balance. Minimum of 100.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
