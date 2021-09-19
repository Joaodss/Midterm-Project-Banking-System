package com.ironhack.midterm.repository.request;

import com.ironhack.midterm.dao.request.RequestNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestNewRepository
    extends RequestBaseRepository<RequestNew>, JpaRepository<RequestNew, Long> {
}
