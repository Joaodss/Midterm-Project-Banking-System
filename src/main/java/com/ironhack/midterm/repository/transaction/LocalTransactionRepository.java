package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.transaction.LocalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalTransactionRepository
    extends TransactionBaseRepository<LocalTransaction>, JpaRepository<LocalTransaction, Long> {


}
