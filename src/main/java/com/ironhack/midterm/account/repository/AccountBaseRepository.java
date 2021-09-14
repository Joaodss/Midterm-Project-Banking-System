package com.ironhack.midterm.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface AccountBaseRepository<T> extends JpaRepository<T, Long> {

    List<T> findAllJoined();

    Optional<T> findByIdJoined(long id);


}
