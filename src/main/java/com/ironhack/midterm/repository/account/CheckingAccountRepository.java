package com.ironhack.midterm.repository.account;

import com.ironhack.midterm.dao.account.CheckingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckingAccountRepository
    extends AccountBaseRepository<CheckingAccount>, JpaRepository<CheckingAccount, Long> {


}
