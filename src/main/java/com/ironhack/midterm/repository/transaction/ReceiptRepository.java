package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.transaction.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

  @Query("SELECT r FROM Receipt r " +
      "LEFT JOIN FETCH r.personalAccount pa " +
      "LEFT JOIN FETCH r.externalAccount ea " +
      "LEFT JOIN FETCH r.transaction t " +
      "WHERE r.id = :id")
  Optional<Receipt> findByIdJoined(long id);

  @Query("SELECT r FROM Receipt r " +
      "LEFT JOIN FETCH r.personalAccount pa " +
      "LEFT JOIN FETCH r.externalAccount ea " +
      "LEFT JOIN FETCH r.transaction t " +
      "WHERE pa.id = :accountId AND " +
      "t.id = :transactionId")
  Optional<Receipt> findByTransactionIdJoined(long accountId, long transactionId);

}
