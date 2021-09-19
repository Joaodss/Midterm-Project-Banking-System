package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.transaction.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositRepository
    extends TransactionBaseRepository<Deposit>, JpaRepository<Deposit, Long> {


}
