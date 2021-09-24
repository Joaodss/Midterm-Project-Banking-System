package com.ironhack.midterm.repository.account;

import com.ironhack.midterm.dao.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface AccountBaseRepository<T extends Account> extends JpaRepository<T, Long> {
  // Used by all user account based repositories.

  @Query("SELECT e FROM #{#entityName} e " +
      "LEFT JOIN FETCH e.primaryOwner p " +
      "LEFT JOIN FETCH e.secondaryOwner s")
  List<T> findAllJoined();

  @Query("SELECT e FROM #{#entityName} e " +
      "LEFT JOIN FETCH e.primaryOwner p " +
      "LEFT JOIN FETCH e.secondaryOwner s " +
      "WHERE e.id = :id")
  Optional<T> findByIdJoined(long id);

  @Query("SELECT e FROM #{#entityName} e " +
      "LEFT JOIN FETCH e.primaryOwner p " +
      "LEFT JOIN FETCH e.secondaryOwner s " +
      "WHERE p.username = :username OR " +
      "s.username = :username")
  List<T> findAllByUsernameJoined(String username);

}
