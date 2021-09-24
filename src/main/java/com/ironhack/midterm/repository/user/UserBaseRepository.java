package com.ironhack.midterm.repository.user;

import com.ironhack.midterm.dao.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface UserBaseRepository<T extends User> extends JpaRepository<T, Long> {

  Optional<T> findByUsername(String username);

}
