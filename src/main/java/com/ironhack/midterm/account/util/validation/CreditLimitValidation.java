package com.ironhack.midterm.account.util.validation;

import com.ironhack.midterm.account.model.Money;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class CreditLimitValidation implements ConstraintValidator<CreditLimitConstrain, Money> {

    @Override
    public void initialize(CreditLimitConstrain constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Money money, ConstraintValidatorContext constraintValidatorContext) {
        BigDecimal bd = money.getAmount();
        return bd.compareTo(BigDecimal.ZERO) >= 0 && bd.compareTo(new BigDecimal("100000.00")) <= 0;
    }


}
