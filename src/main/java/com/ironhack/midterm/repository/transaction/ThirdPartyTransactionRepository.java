package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.transaction.ThirdPartyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdPartyTransactionRepository
    extends TransactionBaseRepository<ThirdPartyTransaction>, JpaRepository<ThirdPartyTransaction, Long> {

}
