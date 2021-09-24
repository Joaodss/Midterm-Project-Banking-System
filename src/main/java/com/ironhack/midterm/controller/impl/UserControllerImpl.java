package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.UserController;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.dao.user.ThirdParty;
import com.ironhack.midterm.dao.user.User;
import com.ironhack.midterm.dto.*;
import com.ironhack.midterm.service.user.AccountHolderService;
import com.ironhack.midterm.service.user.AdminService;
import com.ironhack.midterm.service.user.ThirdPartyService;
import com.ironhack.midterm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserControllerImpl implements UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private AdminService adminService;

  @Autowired
  private AccountHolderService accountHolderService;

  @Autowired
  private ThirdPartyService thirdPartyService;


  // ======================================== GET Methods ========================================
  // -------------------- Global USER --------------------
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<User> getUsers() {
    try {
      return userService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/id/{id}")
  @ResponseStatus(HttpStatus.OK)
  public User getUserById(@PathVariable("id") long id) {
    try {
      return userService.getById(id);
    } catch (InstanceNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/{username}")
  @ResponseStatus(HttpStatus.OK)
  public User getUserByUsername(@PathVariable("username") String username) {
    try {
      return userService.getByUsername(username);
    } catch (InstanceNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- Admin --------------------
  @GetMapping("/admins")
  @ResponseStatus(HttpStatus.OK)
  public List<Admin> getAdmins() {
    try {
      return adminService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- Account Holder --------------------
  @GetMapping("/account_holders")
  @ResponseStatus(HttpStatus.OK)
  public List<AccountHolder> getAccountHolders() {
    try {
      return accountHolderService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- Third Party --------------------
  @GetMapping("/third_parties")
  @ResponseStatus(HttpStatus.OK)
  public List<ThirdParty> getAThirdParties() {
    try {
      return thirdPartyService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  // ======================================== POST Methods ========================================
  // -------------------- New Admin [ADMIN] --------------------
  @PostMapping("/new_admin")
  @ResponseStatus(HttpStatus.CREATED)
  public void createNewAdmin(@RequestBody @Valid AdminDTO admin) {
    try {
      adminService.newUser(admin);
    } catch (InstanceAlreadyExistsException e1) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- New Account Holder [PUBLIC] --------------------
  @PostMapping("/new")
  @ResponseStatus(HttpStatus.CREATED)
  public void createNewAccountHolder(@RequestBody @Valid AccountHolderDTO accountHolder) {
    try {
      accountHolderService.newUser(accountHolder);
    } catch (InstanceAlreadyExistsException e1) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // -------------------- New Third Party [ADMIN] --------------------
  @PostMapping("/new_third_party")
  @ResponseStatus(HttpStatus.CREATED)
  public void createNewThirdParty(@RequestBody @Valid ThirdPartyDTO thirdParty) {
    try {
      thirdPartyService.newUser(thirdParty);
    } catch (InstanceAlreadyExistsException e1) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  // ======================================== PUT Methods ========================================


  // ======================================== PATCH Methods ========================================
  @PatchMapping("/{username}/change_password")
  @ResponseStatus(HttpStatus.OK)
  public void editPassword(@PathVariable("username") String username, @RequestBody @Valid UserPasswordDTO userPassword) {
    try {
      userService.changePassword(username, userPassword);
    } catch (IllegalArgumentException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    } catch (InstanceNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PatchMapping("/{username}/edit")
  @ResponseStatus(HttpStatus.OK)
  public void editUser(String username, UserDTO user) {
    try {
      userService.edit(username, user);
    } catch (InstanceNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }


  // ======================================== DELETE Methods ========================================
  @DeleteMapping("/delete/{id}/{username}/{password}")
  @ResponseStatus(HttpStatus.OK)
  public void deleteUser(long id, String username, String password) {
    try {
      userService.delete(id, username, password);
    } catch (IllegalArgumentException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    } catch (InstanceNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


}
