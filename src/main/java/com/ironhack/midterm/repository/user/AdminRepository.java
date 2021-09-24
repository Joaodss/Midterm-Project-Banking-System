package com.ironhack.midterm.repository.user;

import com.ironhack.midterm.dao.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository
    extends UserBaseRepository<Admin>, JpaRepository<Admin, Long> {

}
