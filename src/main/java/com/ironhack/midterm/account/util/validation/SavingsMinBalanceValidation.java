package com.ironhack.midterm.account.util.validation;

import com.ironhack.midterm.account.model.Money;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class SavingsMinBalanceValidation implements ConstraintValidator<SavingsMinBalanceConstrain, Money> {

    @Override
    public void initialize(SavingsMinBalanceConstrain constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Money money, ConstraintValidatorContext constraintValidatorContext) {
        BigDecimal bd = money.getAmount();
        return bd.compareTo(new BigDecimal("100.00")) >= 0;
    }
}
