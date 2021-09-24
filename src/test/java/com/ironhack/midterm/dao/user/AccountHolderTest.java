package com.ironhack.midterm.dao.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.model.Address;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class AccountHolderTest {
  @Test
  void testConstructor() {
    LocalDate dateOfBirth = LocalDate.ofEpochDay(1L);
    AccountHolder actualAccountHolder = new AccountHolder("janedoe", "iloveyou", "Name", dateOfBirth,
        new Address("42 Main St", "Postal Code", "Oxford", "GB"));

    assertEquals("1970-01-02", actualAccountHolder.getDateOfBirth().toString());
    assertEquals("AccountHolder(super=User(id=null, username=janedoe, name=[], name=Name), dateOfBirth=1970-01-02,"
        + " primaryAddress=Address(streetAddress=42 Main St, postalCode=Postal Code, city=Oxford, country=GB),"
        + " mailingAddress=null)", actualAccountHolder.toString());
    assertEquals("janedoe", actualAccountHolder.getUsername());
    List<Account> secondaryAccounts = actualAccountHolder.getSecondaryAccounts();
    assertTrue(secondaryAccounts.isEmpty());
    assertTrue(actualAccountHolder.getRoles().isEmpty());
    assertEquals(secondaryAccounts, actualAccountHolder.getPrimaryAccounts());
    assertEquals("Name", actualAccountHolder.getName());
  }

  @Test
  void testConstructor2() {
    LocalDate dateOfBirth = LocalDate.ofEpochDay(1L);
    Address primaryAddress = new Address("42 Main St", "Postal Code", "Oxford", "GB");

    Address address = new Address("42 Main St", "Postal Code", "Oxford", "GB");

    AccountHolder actualAccountHolder = new AccountHolder("janedoe", "iloveyou", "Name", dateOfBirth, primaryAddress,
        address);

    assertEquals("1970-01-02", actualAccountHolder.getDateOfBirth().toString());
    assertEquals("janedoe", actualAccountHolder.getUsername());
    List<Account> secondaryAccounts = actualAccountHolder.getSecondaryAccounts();
    assertTrue(secondaryAccounts.isEmpty());
    assertTrue(actualAccountHolder.getRoles().isEmpty());
    Address primaryAddress1 = actualAccountHolder.getPrimaryAddress();
    assertEquals(address, primaryAddress1);
    assertEquals(secondaryAccounts, actualAccountHolder.getPrimaryAccounts());
    assertEquals("Name", actualAccountHolder.getName());
    assertEquals(primaryAddress1, actualAccountHolder.getMailingAddress());
  }
}

