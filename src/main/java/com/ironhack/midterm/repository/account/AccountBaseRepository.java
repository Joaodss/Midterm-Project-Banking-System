package com.ironhack.midterm.repository.account;

import com.ironhack.midterm.dao.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface AccountBaseRepository<T extends Account> extends JpaRepository<T, Long> {
  @Query("SELECT e FROM #{#entityName} e " +
      "LEFT JOIN FETCH e.primaryOwner " +
      "LEFT JOIN FETCH e.secondaryOwner")
  List<T> findAllJoined();

  @Query("SELECT e FROM #{#entityName} e " +
      "LEFT JOIN FETCH e.primaryOwner " +
      "WHERE e.id = :id")
  Optional<T> findByIdJoined(long id);


}
