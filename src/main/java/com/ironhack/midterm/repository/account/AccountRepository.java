package com.ironhack.midterm.repository.account;

import com.ironhack.midterm.dao.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository
    extends AccountBaseRepository<Account>, JpaRepository<Account, Long> {

}
