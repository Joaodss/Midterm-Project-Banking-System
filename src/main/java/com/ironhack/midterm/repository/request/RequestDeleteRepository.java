package com.ironhack.midterm.repository.request;

import com.ironhack.midterm.dao.request.RequestDelete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestDeleteRepository
    extends RequestBaseRepository<RequestDelete>, JpaRepository<RequestDelete, Long> {



  }
