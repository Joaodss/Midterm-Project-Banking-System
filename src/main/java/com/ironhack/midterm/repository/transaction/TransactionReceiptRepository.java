package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.transaction.TransactionReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionReceiptRepository extends JpaRepository<TransactionReceipt, Long> {
}
