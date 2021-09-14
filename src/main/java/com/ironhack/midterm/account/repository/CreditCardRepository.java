package com.ironhack.midterm.account.repository;

import com.ironhack.midterm.account.dao.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardRepository
        extends AccountBaseRepository<CreditCard>, JpaRepository<CreditCard, Long> {


}
