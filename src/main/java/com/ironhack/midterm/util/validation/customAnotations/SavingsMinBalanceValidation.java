package com.ironhack.midterm.util.validation.customAnotations;

import com.ironhack.midterm.model.Money;

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
    return money.getAmount().compareTo(new BigDecimal("100.00")) >= 0;
  }
}
