package com.ironhack.midterm.dao.request;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "request_delete")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestDelete extends Request {

  @NotNull
  @JsonIncludeProperties(value = {"id"})
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_account_id")
  private Account targetedAccount;


  // ======================================== CONSTRUCTORS ========================================
  public RequestDelete(AccountHolder user, RequestStatus requestStatus, String description, Account targetedAccount) {
    super(user, requestStatus, description);
    this.targetedAccount = targetedAccount;
  }

  public RequestDelete(AccountHolder user, String description, Account targetedAccount) {
    super(user, description);
    this.targetedAccount = targetedAccount;
  }


  // ======================================== METHODS ========================================

  // ======================================== OVERRIDE METHODS ========================================


}
