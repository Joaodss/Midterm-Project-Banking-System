package com.ironhack.midterm.user.repository;

import com.ironhack.midterm.user.dao.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {


}
