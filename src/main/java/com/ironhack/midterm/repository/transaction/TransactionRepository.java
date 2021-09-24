package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository
    extends TransactionBaseRepository<Transaction>, JpaRepository<Transaction, Long> {

}
