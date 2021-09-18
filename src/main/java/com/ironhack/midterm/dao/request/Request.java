package com.ironhack.midterm.dao.request;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "request")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class Request {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @JsonIncludeProperties(value = {"id", "name"})
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private AccountHolder user;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "request_status")
  private RequestStatus requestStatus;

  @Column(name = "description")
  private String description;


  // ======================================== CONSTRUCTORS ========================================
  public Request(AccountHolder user, RequestStatus requestStatus, String description) {
    this.user = user;
    this.requestStatus = requestStatus;
    this.description = description;
  }

  public Request(AccountHolder user, String description) {
    this.user = user;
    this.requestStatus = RequestStatus.PROCESSING;
    this.description = description;
  }


  // ======================================== METHODS ========================================

  // ======================================== OVERRIDE METHODS ========================================


}
