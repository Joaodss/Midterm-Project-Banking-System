package com.ironhack.midterm.security.service;

import com.ironhack.midterm.dao.account.Account;
import com.ironhack.midterm.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;

import javax.management.InstanceNotFoundException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

public class ApiGuardService {

  @Autowired
  private HttpServletRequest request;

  @Autowired
  private AccountService accountService;

  public boolean checkUsernameOrIfAdmin(Authentication authentication, String username) throws AuthenticationException {
    return (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER")) && authentication.getName().equals(username)) ||
        authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
  }

  public boolean checkUsernameFromAccountIdOrIfAdmin(Authentication authentication, String id) throws AuthenticationException {
    Account account;

    try {
      account = accountService.getById(Long.parseLong(id));
    } catch (NumberFormatException e1) {
      return authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
    } catch (EntityNotFoundException e2) {
      if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")))
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Account id noty found.");
      return false;
    }

    String primaryAccountUsername = account.getPrimaryOwner().getUsername();
    String secondaryAccountUsername = null;
    if (account.getSecondaryOwner() != null) secondaryAccountUsername = account.getSecondaryOwner().getUsername();

    return (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER")) &&
        authentication.getName().equals(primaryAccountUsername) || authentication.getName().equals(secondaryAccountUsername)) ||
        authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
  }

}
