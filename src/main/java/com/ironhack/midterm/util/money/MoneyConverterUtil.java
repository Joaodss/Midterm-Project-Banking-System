package com.ironhack.midterm.util.money;

import com.ironhack.midterm.model.Money;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.money.convert.MonetaryConversions;
import java.util.Currency;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MoneyConverterUtil {

  public static boolean isSameCurrency(Currency baseCurrency, Money comparativeMoney) {
    return baseCurrency.getCurrencyCode().equals(comparativeMoney.getCurrency().getCurrencyCode());
  }

  public static boolean isSameCurrency(Money baseMoney, Money comparativeMoney) {
    return baseMoney.getCurrency().getCurrencyCode().equals(comparativeMoney.getCurrency().getCurrencyCode());
  }

  public static Money convertCurrency(Currency baseCurrency, Money convertMoney) {
    if (!isSameCurrency(baseCurrency, convertMoney)) {
      var rateProvider = MonetaryConversions.getExchangeRateProvider();
      var conversion = rateProvider.getCurrencyConversion(baseCurrency.getCurrencyCode());
      var newConvertMoney = org.javamoney.moneta.Money.of(convertMoney.getAmount(), convertMoney.getCurrency().getCurrencyCode());
      var convertedMoney = newConvertMoney.with(conversion);
      return new Money(convertedMoney.getNumberStripped(), Currency.getInstance(convertedMoney.getCurrency().getCurrencyCode()));
    }
    return convertMoney;
  }

  public static Money convertCurrency(Money baseCurrency, Money convertMoney) {
    if (!isSameCurrency(baseCurrency, convertMoney)) {
      var rateProvider = MonetaryConversions.getExchangeRateProvider();
      var conversion = rateProvider.getCurrencyConversion(baseCurrency.getCurrency().getCurrencyCode());
      var newConvertMoney = org.javamoney.moneta.Money.of(convertMoney.getAmount(), convertMoney.getCurrency().getCurrencyCode());
      var convertedMoney = newConvertMoney.with(conversion);
      return new Money(convertedMoney.getNumberStripped(), Currency.getInstance(convertedMoney.getCurrency().getCurrencyCode()));
    }
    return convertMoney;
  }


}
