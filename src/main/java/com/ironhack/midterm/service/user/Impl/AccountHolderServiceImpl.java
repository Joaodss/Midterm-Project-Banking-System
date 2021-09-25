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

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
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


  // ======================================== ADD USERS Methods ========================================
  public void newUser(UserAccountHolderDTO accountHolder) throws EntityExistsException {
    // Check if username already exists
    if (userService.isUsernamePresent(accountHolder.getUsername()))
      throw new EntityExistsException("Username already exists.");

    Address pa = new Address(
        accountHolder.getPaStreetAddress().trim(), accountHolder.getPaPostalCode().trim(), accountHolder.getPaCity().trim(), accountHolder.getPaCountry().trim());
    Address ma = null;
    if (accountHolder.getMaStreetAddress() != null || accountHolder.getMaPostalCode() != null || accountHolder.getMaCity() != null || accountHolder.getMaCountry() != null)
      ma = new Address(accountHolder.getMaStreetAddress().trim(), accountHolder.getMaPostalCode().trim(), accountHolder.getMaCity().trim(), accountHolder.getMaCountry().trim());

    AccountHolder ah = new AccountHolder(
        accountHolder.getUsername().trim(), accountHolder.getPassword().trim(), accountHolder.getName().trim(), accountHolder.getDateOfBirth(), pa, ma);

    // Set "USER" role
    Optional<Role> userRole = roleService.getByName("USER");
    if (userRole.isPresent()) {
      ah.getRoles().add(userRole.get());
    } else {
      roleService.newRole("USER");
      Optional<Role> newUserRole = roleService.getByName("USER");
      newUserRole.ifPresent(role -> ah.getRoles().add(role));
    }
    accountHolderRepository.save(ah);
  }


  // ======================================== UTIL Methods ========================================
  public AccountHolder[] findAccountHolders(AccountDTO account) throws EntityNotFoundException, IllegalArgumentException {
    // Check if primary owner exists and id and username match.
    Optional<AccountHolder> primaryOwner = accountHolderRepository.findByUsername(account.getPrimaryOwnerUsername());
    if (primaryOwner.isEmpty()) throw new EntityNotFoundException("Primary owner user not found by username.");
    if (!Objects.equals(primaryOwner.get().getId(), account.getPrimaryOwnerId()))
      throw new IllegalArgumentException("Primary owner's username and id do not match.");

    // Check if secondary owner exists and id and username match. Only if secondary owner id and name inputs are present.
    Optional<AccountHolder> secondaryOwner = Optional.empty();
    if (account.getSecondaryOwnerId() != null && account.getSecondaryOwnerUsername() != null) {
      secondaryOwner = accountHolderRepository.findByUsername(account.getSecondaryOwnerUsername());
      if (secondaryOwner.isEmpty()) throw new EntityNotFoundException("Secondary owner user not found by username.");
      if (!Objects.equals(secondaryOwner.get().getId(), account.getSecondaryOwnerId()))
        throw new IllegalArgumentException("Secondary owner's username and id do not match.");
    }

    // If equal, secondary is null.
    if (secondaryOwner.isPresent() && primaryOwner.get() == secondaryOwner.get())
      secondaryOwner = Optional.empty();

    AccountHolder[] accountHolders = new AccountHolder[2];
    accountHolders[0] = primaryOwner.get();
    accountHolders[1] = secondaryOwner.orElse(null);
    return accountHolders;
  }


}
