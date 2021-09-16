package com.ironhack.midterm.dao.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.model.Address;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
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
@ToString(callSuper = true)
public class AccountHolder extends User {

  @NotNull
  @Past
  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  // TODO JA - check if not null applies only here
  @NotNull
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "streetAddress", column = @Column(name = "pa_street", nullable = false)),
      @AttributeOverride(name = "postalCode", column = @Column(name = "pa_postal_code", nullable = false)),
      @AttributeOverride(name = "city", column = @Column(name = "pa_city", nullable = false)),
      @AttributeOverride(name = "country", column = @Column(name = "pa_country", nullable = false)),
  })
  private Address primaryAddress;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "streetAddress", column = @Column(name = "ma_street")),
      @AttributeOverride(name = "postalCode", column = @Column(name = "ma_postal_code")),
      @AttributeOverride(name = "city", column = @Column(name = "ma_city")),
      @AttributeOverride(name = "country", column = @Column(name = "ma_country")),
  })
  private Address mailingAddress;

  @JsonIgnoreProperties(value = {"primaryOwner", "secondaryOwner"}, allowSetters = true)
  @ToString.Exclude
  @OneToMany(mappedBy = "primaryOwner", cascade = {})
  private List<Account> primaryAccounts = new ArrayList<>();

  @JsonIgnoreProperties(value = {"primaryOwner", "secondaryOwner"}, allowSetters = true)
  @ToString.Exclude
  @OneToMany(mappedBy = "secondaryOwner", cascade = {})
  private List<Account> secondaryAccounts = new ArrayList<>();


  // ======================================== Constructors ========================================
  public AccountHolder(String username, String password, String name, LocalDate dateOfBirth, Address primaryAddress, Address mailingAddress) {
    super(username, password, name);
    this.dateOfBirth = dateOfBirth;
    this.primaryAddress = primaryAddress;
    this.mailingAddress = mailingAddress;
  }

  public AccountHolder(String username, String password, String name, LocalDate dateOfBirth, Address primaryAddress) {
    super(username, password, name);
    this.dateOfBirth = dateOfBirth;
    this.primaryAddress = primaryAddress;
  }


  // ======================================== Getters & Setters ========================================


  // ======================================== Override Methods ========================================
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
