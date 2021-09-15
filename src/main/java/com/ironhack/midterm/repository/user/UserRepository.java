package com.ironhack.midterm.repository.user;

import com.ironhack.midterm.dao.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends UserBaseRepository<User>, JpaRepository<User, Long> {


}
