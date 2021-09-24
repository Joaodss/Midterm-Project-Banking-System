package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface TransactionBaseRepository<T extends Transaction> extends JpaRepository<T, Long> {

  @Query("SELECT e FROM #{#entityName} e " +
      "LEFT JOIN FETCH e.baseAccount ba " +
      "LEFT JOIN FETCH e.targetAccount ta " +
      "WHERE e.id = :id")
  Optional<T> findByIdJoined(long id);

  @Query("SELECT e FROM #{#entityName} e " +
      "LEFT JOIN FETCH e.baseAccount ba " +
      "LEFT JOIN FETCH e.targetAccount ta " +
      "WHERE ba.id = :id " +
      "OR ta.id = :id " +
      "ORDER BY t.operationDate DESC")
  List<T> findAllByAccountIdJoined(long id);

}
