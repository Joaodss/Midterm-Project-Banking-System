package com.ironhack.midterm.repository.account;

import com.ironhack.midterm.dao.account.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingAccountRepository
    extends AccountBaseRepository<SavingsAccount>, JpaRepository<SavingsAccount, Long> {


}
