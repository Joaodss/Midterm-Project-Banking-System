package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.transaction.InterestTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestTransactionRepository
    extends TransactionBaseRepository<InterestTransaction>, JpaRepository<InterestTransaction, Long> {


}
