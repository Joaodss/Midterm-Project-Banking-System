package com.ironhack.midterm.util;

import com.ironhack.midterm.dao.account.Account;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

public class AuthorisationUtil {

  public static boolean isAccountOwnerOrAdmin(Authentication auth, Account account){
    List<String> validUsernames = new ArrayList<>();
    validUsernames.add(account.getPrimaryOwner().getUsername());
    if (account.getSecondaryOwner() != null) validUsernames.add(account.getSecondaryOwner().getUsername());

    return auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")) ||
        validUsernames.contains(auth.getName());
  }

}
