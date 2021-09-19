package com.ironhack.midterm.repository.request;

import com.ironhack.midterm.dao.request.RequestAccountEdit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestAccountEditRepository
    extends RequestBaseRepository<RequestAccountEdit>, JpaRepository<RequestAccountEdit, Long> {


}
