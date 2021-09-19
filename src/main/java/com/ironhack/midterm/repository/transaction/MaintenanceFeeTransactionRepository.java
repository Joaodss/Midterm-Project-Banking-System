package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.transaction.MaintenanceFeeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceFeeTransactionRepository
    extends TransactionBaseRepository<MaintenanceFeeTransaction>, JpaRepository<MaintenanceFeeTransaction, Long> {


}
