package com.ironhack.midterm.account.repository;

import com.ironhack.midterm.account.dao.StudentCheckingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentCheckingAccountRepository
        extends AccountBaseRepository<StudentCheckingAccount>, JpaRepository<StudentCheckingAccount, Long> {


}
