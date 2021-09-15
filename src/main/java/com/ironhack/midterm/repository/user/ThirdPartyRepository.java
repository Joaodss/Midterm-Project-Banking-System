package com.ironhack.midterm.repository.user;

import com.ironhack.midterm.dao.user.ThirdParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdPartyRepository
    extends UserBaseRepository<ThirdParty>, JpaRepository<ThirdParty, Long> {


}
