package com.ironhack.midterm.repository.account;

import com.ironhack.midterm.dao.account.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardRepository
    extends AccountBaseRepository<CreditCard>, JpaRepository<CreditCard, Long> {


}
