package com.ironhack.midterm.repository.transaction;

import com.ironhack.midterm.dao.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface TransactionBaseRepository<T extends Transaction> extends JpaRepository<T, Long> {

  @Query("SELECT e FROM #{#entityName} e " +
      "LEFT JOIN FETCH e.baseAccount ba " +
      "LEFT JOIN FETCH e.targetAccount ta " +
      "WHERE e.id = :id")
  Optional<Transaction> findByIdJoined(long id);

}
