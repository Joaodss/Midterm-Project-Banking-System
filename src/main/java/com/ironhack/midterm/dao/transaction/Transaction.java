package com.ironhack.midterm.dao.transaction;

import com.ironhack.midterm.enums.Status;
import com.ironhack.midterm.model.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import static com.ironhack.midterm.util.validation.DateTimeUtil.dateTimeNow;

@Entity
@Table(name = "transaction")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Valid
  @NotNull
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "base_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "base_currency", nullable = false))
  })
  private Money baseAmount;

  @Valid
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "amount", column = @Column(name = "converted_amount", nullable = false)),
      @AttributeOverride(name = "currency", column = @Column(name = "converted_currency", nullable = false))
  })
  private Money convertedAmount;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private Status status;

  @NotNull
  @Column(name = "operation_date")
  private LocalDateTime operationDate;

  @OneToMany(mappedBy = "transaction", cascade = {CascadeType.REMOVE})
  private List<TransactionReceipt> receipts;


  // ======================================== CONSTRUCTORS ========================================
  public Transaction(Money baseAmount, Money convertedAmount) {
    this.baseAmount = baseAmount;
    this.convertedAmount = convertedAmount;
    this.status = Status.PROCESSING;
    this.operationDate = dateTimeNow();
  }

  public Transaction(Money baseAmount) {
    this.baseAmount = baseAmount;
    this.status = Status.PROCESSING;
    this.operationDate = dateTimeNow();
  }


  // ======================================== METHODS ========================================

  // ======================================== OVERRIDE METHODS ========================================

//  @Override
//  public String toString() {
//    return "Transaction{" +
//        "id=" + id +
//        ", account=" + ownerAccount.getId() +
//        ", accountPrimaryOwner=" + ownerAccount.getPrimaryOwner() + ": " + ownerAccount.getPrimaryOwner().getName() +
//        (ownerAccount.getSecondaryOwner() == null ? "" :
//            ", accountSecondaryOwner=" + ownerAccount.getSecondaryOwner().getId() + ": " +
//                ownerAccount.getSecondaryOwner().getName()) +
//        ", baseAmount=" + baseAmount.toString() +
//        (convertedAmount == null ? "" : ", convertedAmount=" + convertedAmount) +
//        ", operationDate=" + operationDate +
//        '}';
//  }
}
