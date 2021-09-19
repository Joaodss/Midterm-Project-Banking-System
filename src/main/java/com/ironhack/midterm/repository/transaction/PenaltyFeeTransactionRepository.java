package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.transaction.PenaltyFeeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PenaltyFeeTransactionRepository
    extends TransactionBaseRepository<PenaltyFeeTransaction>, JpaRepository<PenaltyFeeTransaction, Long> {


}
