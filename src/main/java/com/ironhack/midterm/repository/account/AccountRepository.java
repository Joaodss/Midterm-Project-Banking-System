package com.ironhack.midterm.repository.account;

import com.ironhack.midterm.dao.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// TODO JA -change account base here if this not in use
@Repository
public interface AccountRepository
    extends AccountBaseRepository<Account>, JpaRepository<Account, Long> {




}
