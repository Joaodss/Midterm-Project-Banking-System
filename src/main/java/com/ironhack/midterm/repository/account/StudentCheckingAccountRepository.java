package com.ironhack.midterm.repository.account;

import com.ironhack.midterm.dao.account.StudentCheckingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentCheckingAccountRepository
    extends AccountBaseRepository<StudentCheckingAccount>, JpaRepository<StudentCheckingAccount, Long> {


}
