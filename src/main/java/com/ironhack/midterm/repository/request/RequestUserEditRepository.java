package com.ironhack.midterm.repository.request;

import com.ironhack.midterm.dao.request.RequestUserEdit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestUserEditRepository
    extends RequestBaseRepository<RequestUserEdit>, JpaRepository<RequestUserEdit, Long> {

}
