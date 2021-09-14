package com.ironhack.midterm.account.repository;

import com.ironhack.midterm.account.dao.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingAccountRepository extends JpaRepository<SavingsAccount, Long> {


}
