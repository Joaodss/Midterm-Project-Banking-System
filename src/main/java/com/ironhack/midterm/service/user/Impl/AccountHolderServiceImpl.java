package com.ironhack.midterm.service.user.Impl;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.Role;
import com.ironhack.midterm.dto.AccountHolderDTO;
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
  public void newUser(AccountHolderDTO accountHolder) throws InstanceAlreadyExistsException {
    // Check if username already exists
    if (userService.isUsernamePresent(accountHolder.getUsername())) throw new InstanceAlreadyExistsException();

    Address pa = new Address(
        accountHolder.getPaStreetAddress(), accountHolder.getPaPostalCode(), accountHolder.getPaCity(), accountHolder.getPaCountry());
    Address ma = new Address(
        accountHolder.getMaStreetAddress(), accountHolder.getMaPostalCode(), accountHolder.getMaCity(), accountHolder.getMaCountry());
    AccountHolder ah = new AccountHolder(
        accountHolder.getUsername(), accountHolder.getPassword(), accountHolder.getName(), accountHolder.getDateOfBirth(), pa, ma);

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


}
