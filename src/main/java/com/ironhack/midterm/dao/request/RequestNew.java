package com.ironhack.midterm.dao.request;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.AccountType;
import com.ironhack.midterm.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "request_new")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestNew extends Request {

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "request_account_type")
  private AccountType accountType;


  // ======================================== CONSTRUCTORS ========================================
  public RequestNew(AccountHolder user, Status status, String description, AccountType accountType) {
    super(user, status, description);
    this.accountType = accountType;
  }

  public RequestNew(AccountHolder user, String description, AccountType accountType) {
    super(user, description);
    this.accountType = accountType;
  }


  // ======================================== METHODS ========================================

  // ======================================== OVERRIDE METHODS ========================================


}
