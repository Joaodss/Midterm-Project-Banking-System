package com.ironhack.midterm.repository.request;

import com.ironhack.midterm.dao.request.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RequestBaseRepository <T extends Request> extends JpaRepository<T, Long> {


}
