package com.ironhack.midterm.repository.user;

import com.ironhack.midterm.dao.user.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountHolderRepository
    extends UserBaseRepository<AccountHolder>, JpaRepository<AccountHolder, Long> {


}
