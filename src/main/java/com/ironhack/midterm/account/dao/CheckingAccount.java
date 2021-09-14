package com.ironhack.midterm.account.dao;

import com.ironhack.midterm.account.model.Money;
import com.ironhack.midterm.user.dao.AccountHolder;
import com.ironhack.midterm.account.enums.Status;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "checking_account")
@PrimaryKeyJoinColumn(name = "id")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CheckingAccount extends AccountType {

    @NotNull
    @NotBlank
    @Column(name = "secret_key")
    private String secretKey;

    @Valid
    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "min_balance_amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "min_balance_currency", nullable = false))
    })
    private Money minimumBalance = new Money(new BigDecimal("250.00"));

    @Valid
    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "monthly_maintenance_fee_amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "monthly_maintenance_fee_currency", nullable = false))
    })
    private Money monthlyMaintenanceFee = new Money(new BigDecimal("12.00"));

    @NotNull
    @PastOrPresent
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;


    // ======================================== Constructors ========================================
    // ==================== Constructors with default creditLimit/interestRate ====================
    public CheckingAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, String secretKey) {
        super(balance, primaryOwner, secondaryOwner);
        this.secretKey = secretKey;
        this.creationDate = LocalDateTime.now(ZoneId.of("Europe/London"));
        this.status = Status.ACTIVE;
    }

    public CheckingAccount(Money balance, AccountHolder primaryOwner, String secretKey) {
        super(balance, primaryOwner);
        this.secretKey = secretKey;
        this.creationDate = LocalDateTime.now(ZoneId.of("Europe/London"));
        this.status = Status.ACTIVE;
    }
}
