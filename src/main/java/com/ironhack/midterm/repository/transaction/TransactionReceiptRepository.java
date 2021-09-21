package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.transaction.TransactionReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionReceiptRepository extends JpaRepository<TransactionReceipt, Long> {

  @Query("SELECT tr FROM TransactionReceipt tr " +
      "LEFT JOIN FETCH tr.personalAccount pa " +
      "LEFT JOIN FETCH tr.externalAccount ea " +
      "LEFT JOIN FETCH tr.transaction t " +
      "WHERE pa.id = :id " +
      "ORDER BY t.operationDate DESC")
  List<TransactionReceipt> findAllByAccountIdJoined(long id);


  @Query("SELECT tr FROM TransactionReceipt tr " +
      "LEFT JOIN FETCH tr.personalAccount pa " +
      "LEFT JOIN FETCH tr.externalAccount ea " +
      "LEFT JOIN FETCH tr.transaction t " +
      "WHERE tr.id = :id")
  Optional<TransactionReceipt> findByIdJoined(long id);

}
