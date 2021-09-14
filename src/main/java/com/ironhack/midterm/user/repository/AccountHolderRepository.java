package com.ironhack.midterm.user.repository;

import com.ironhack.midterm.user.dao.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {


}
