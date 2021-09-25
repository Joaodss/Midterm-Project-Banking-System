package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  @Query("SELECT t FROM Transaction t " +
      "LEFT JOIN FETCH t.baseAccount ba " +
      "LEFT JOIN FETCH t.targetAccount ta " +
      "LEFT JOIN FETCH t.targetOwner to " +
      "WHERE t.id = :id")
  Optional<Transaction> findByIdJoined(long id);

  @Query("SELECT t FROM Transaction t " +
      "LEFT JOIN FETCH t.baseAccount ba " +
      "LEFT JOIN FETCH t.targetAccount ta " +
      "WHERE ba.id = :id " +
      "OR ta.id = :id " +
      "ORDER BY t.operationDate DESC")
  List<Transaction> findAllByAccountIdJoined(long id);

}
