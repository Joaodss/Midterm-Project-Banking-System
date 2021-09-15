package com.ironhack.midterm.repository.user;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.dao.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBaseRepository<T extends User> extends JpaRepository<T, Long> {



}
