package com.ironhack.midterm.service.user.Impl;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.Role;
import com.ironhack.midterm.dto.AccountDTO;
import com.ironhack.midterm.dto.UserAccountHolderDTO;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.user.AccountHolderRepository;
import com.ironhack.midterm.service.user.AccountHolderService;
import com.ironhack.midterm.service.user.RoleService;
import com.ironhack.midterm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class AccountHolderServiceImpl implements AccountHolderService {

  @Autowired
  private AccountHolderRepository accountHolderRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private RoleService roleService;


  // ======================================== GET USERS Methods ========================================
  public List<AccountHolder> getAll() {
    return accountHolderRepository.findAll();
  }

  public AccountHolder getByUsername(String username) throws InstanceNotFoundException {
    var accountHolder = accountHolderRepository.findByUsername(username);
    if (accountHolder.isPresent()) return accountHolder.get();
    throw new InstanceNotFoundException();
  }

  public boolean isUsernamePresent(String username) {
    return accountHolderRepository.findByUsername(username).isPresent();
  }


  // ======================================== ADD USERS Methods ========================================
  public void newUser(UserAccountHolderDTO accountHolder) throws InstanceAlreadyExistsException {
    // Check if username already exists
    if (userService.isUsernamePresent(accountHolder.getUsername())) throw new InstanceAlreadyExistsException();

    Address pa = new Address(
        accountHolder.getPaStreetAddress().trim(), accountHolder.getPaPostalCode().trim(), accountHolder.getPaCity().trim(), accountHolder.getPaCountry().trim());

    Address ma = null;
    if (accountHolder.getMaStreetAddress() != null || accountHolder.getMaPostalCode() != null || accountHolder.getMaCity() != null || accountHolder.getMaCountry() != null)
      ma = new Address(accountHolder.getMaStreetAddress().trim(), accountHolder.getMaPostalCode().trim(), accountHolder.getMaCity().trim(), accountHolder.getMaCountry().trim());

    AccountHolder ah = new AccountHolder(
        accountHolder.getUsername().trim(), accountHolder.getPassword().trim(), accountHolder.getName().trim(), accountHolder.getDateOfBirth(), pa, ma);

    // Set "USER" role
    Optional<Role> userRole = roleService.getRoleByName("USER");
    if (userRole.isPresent()) {
      ah.getRoles().add(userRole.get());
    } else {
      roleService.addRole("USER");
      Optional<Role> newUserRole = roleService.getRoleByName("USER");
      newUserRole.ifPresent(role -> ah.getRoles().add(role));
    }
    accountHolderRepository.save(ah);
  }


  // ======================================== UTIL Methods ========================================
  public AccountHolder[] getAccountHolders(AccountDTO account, AccountHolderService accountHolderService, UserService userService) throws InstanceNotFoundException, IllegalArgumentException {
    // Check if usernames exists in Account Holders
    if (!accountHolderService.isUsernamePresent(account.getPrimaryOwnerUsername()))
      throw new InstanceNotFoundException("Primary owner user not found by username.");
    if (account.getSecondaryOwnerUsername() != null && !accountHolderService.isUsernamePresent(account.getSecondaryOwnerUsername()))
      throw new InstanceNotFoundException("Secondary owner user not found by username.");

    // Check if username and id match
    try {
      if (!userService.getById(account.getPrimaryOwnerId()).getUsername().equals(account.getPrimaryOwnerUsername()))
        throw new IllegalArgumentException("Primary owner's username and id do not match.");
    } catch (InstanceNotFoundException e1) {
      throw new InstanceNotFoundException("Primary owner's id not found.");
    }
    try {
      if (account.getSecondaryOwnerId() != null && !userService.getById(account.getSecondaryOwnerId()).getUsername().equals(account.getSecondaryOwnerUsername()))
        throw new IllegalArgumentException("Secondary owner's username and id do not match.");
    } catch (InstanceNotFoundException e1) {
      throw new InstanceNotFoundException("Secondary owner's id not found.");
    }

    AccountHolder pah = accountHolderService.getByUsername(account.getPrimaryOwnerUsername());
    AccountHolder sah = null;
    if (!account.getPrimaryOwnerUsername().equals(account.getSecondaryOwnerUsername()) && account.getSecondaryOwnerId() != null && account.getSecondaryOwnerUsername() != null)
      sah = accountHolderService.getByUsername(account.getSecondaryOwnerUsername());

    AccountHolder[] accountHolders = new AccountHolder[2];
    accountHolders[0] = pah;
    accountHolders[1] = sah;
    return accountHolders;
  }


}
