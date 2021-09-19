package com.ironhack.midterm.repository.request;

import com.ironhack.midterm.dao.request.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository
    extends RequestBaseRepository<Request>, JpaRepository<Request, Long> {
}
