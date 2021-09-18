package com.ironhack.midterm.dao.request;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "request_edit_user")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestUserEdit extends Request {


  // ======================================== CONSTRUCTORS ========================================
  public RequestUserEdit(AccountHolder user, Status status, String description) {
    super(user, status, description);
  }

  public RequestUserEdit(AccountHolder user, String description) {
    super(user, description);
  }


  // ======================================== METHODS ========================================

  // ======================================== OVERRIDE METHODS ========================================


}
