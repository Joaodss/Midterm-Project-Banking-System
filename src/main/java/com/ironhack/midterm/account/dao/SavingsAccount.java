package com.ironhack.midterm.account.dao;

import com.ironhack.midterm.account.enums.Status;
import com.ironhack.midterm.account.model.Money;
import com.ironhack.midterm.account.util.validation.SavingsMinBalanceConstrain;
import com.ironhack.midterm.user.dao.AccountHolder;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "savings_account")
@PrimaryKeyJoinColumn(name = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SavingsAccount extends Account {

    @NotNull
    @NotBlank
    @Column(name = "secret_key")
    private String secretKey;

    @Valid
    @NotNull
    @SavingsMinBalanceConstrain
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "min_balance_amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "min_balance_currency", nullable = false))
    })
    private Money minimumBalance;

    @NotNull
    @Digits(integer = 1, fraction = 4)
    @DecimalMax(value = "0.5000")
    private BigDecimal interestRate;

    @NotNull
    @PastOrPresent
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;


    // ======================================== Constructors ========================================
    // ==================== Constructors with default minimumBalance/interestRate/creationDate/status ====================
    public SavingsAccount(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, String secretKey) {
        super(balance, primaryOwner, secondaryOwner);
        this.secretKey = secretKey;
        this.minimumBalance = new Money(new BigDecimal("1000"));
        this.interestRate = new BigDecimal("0.0025");
        this.creationDate = LocalDateTime.now(ZoneId.of("Europe/London"));
        this.status = Status.ACTIVE;
    }

    public SavingsAccount(Money balance, AccountHolder primaryOwner, String secretKey) {
        super(balance, primaryOwner);
        this.secretKey = secretKey;
        this.minimumBalance = new Money(new BigDecimal("1000"));
        this.interestRate = new BigDecimal("0.0025");
        this.creationDate = LocalDateTime.now(ZoneId.of("Europe/London"));
        this.status = Status.ACTIVE;
    }
}
