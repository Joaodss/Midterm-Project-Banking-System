package com.ironhack.midterm.account.repository;

import com.ironhack.midterm.account.dao.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// TODO JA -change account base here if this not in use
@Repository
public interface AccountRepository
        extends AccountBaseRepository<Account>, JpaRepository<Account, Long> {


}
