package com.ironhack.midterm.dao.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.model.Address;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "account_holder")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ToString(callSuper = true)
public class AccountHolder extends User {

  @NotNull
  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @NotNull
  @Valid
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "streetAddress", column = @Column(name = "primary_address_street", nullable = false)),
      @AttributeOverride(name = "postalCode", column = @Column(name = "primary_address_postal_code", nullable = false)),
      @AttributeOverride(name = "city", column = @Column(name = "primary_address_city", nullable = false)),
      @AttributeOverride(name = "country", column = @Column(name = "primary_address_country", nullable = false)),
  })
  private Address primaryAddress;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "streetAddress", column = @Column(name = "mailing_address_street")),
      @AttributeOverride(name = "postalCode", column = @Column(name = "mailing_address_postal_code")),
      @AttributeOverride(name = "city", column = @Column(name = "mailing_address_city")),
      @AttributeOverride(name = "country", column = @Column(name = "mailing_address_country")),
  })
  private Address mailingAddress;

  // ======================================== MAPPING ========================================
  @JsonIncludeProperties(value = {"id", "accountType"})
  @ToString.Exclude
  @OneToMany(mappedBy = "primaryOwner")
  private List<Account> primaryAccounts = new ArrayList<>();

  @JsonIncludeProperties(value = {"id", "accountType"})
  @ToString.Exclude
  @OneToMany(mappedBy = "secondaryOwner")
  private List<Account> secondaryAccounts = new ArrayList<>();


  // ======================================== CONSTRUCTORS ========================================
  // Constructor with primary and mailing addresses.
  public AccountHolder(String username, String password, String name, LocalDate dateOfBirth, Address primaryAddress, Address mailingAddress) {
    super(username, password, name);
    this.dateOfBirth = dateOfBirth;
    this.primaryAddress = primaryAddress;
    this.mailingAddress = mailingAddress;
  }

  // Constructor only with primary address.
  public AccountHolder(String username, String password, String name, LocalDate dateOfBirth, Address primaryAddress) {
    super(username, password, name);
    this.dateOfBirth = dateOfBirth;
    this.primaryAddress = primaryAddress;
  }


  // ======================================== OVERRIDE METHODS ========================================
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    AccountHolder that = (AccountHolder) o;
    return getDateOfBirth().equals(that.getDateOfBirth()) && getPrimaryAddress().equals(that.getPrimaryAddress()) && Objects.equals(getMailingAddress(), that.getMailingAddress());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getDateOfBirth(), getPrimaryAddress(), getMailingAddress());
  }

}
