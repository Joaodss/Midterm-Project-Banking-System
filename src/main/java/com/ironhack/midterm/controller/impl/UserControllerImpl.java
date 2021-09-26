package com.ironhack.midterm.controller.impl;

import com.ironhack.midterm.controller.UserController;
import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.dao.user.ThirdParty;
import com.ironhack.midterm.dao.user.User;
import com.ironhack.midterm.dto.UserAccountHolderDTO;
import com.ironhack.midterm.dto.UserDTO;
import com.ironhack.midterm.dto.UserEditDTO;
import com.ironhack.midterm.dto.UserEditPasswordDTO;
import com.ironhack.midterm.service.user.AccountHolderService;
import com.ironhack.midterm.service.user.AdminService;
import com.ironhack.midterm.service.user.ThirdPartyService;
import com.ironhack.midterm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
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
  // -------------------- All Users [ADMIN] --------------------
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<User> getUsers() {
    try {
      return userService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  // -------------------- Users by Id [ADMIN] --------------------
  @GetMapping("/id/{id}")
  @ResponseStatus(HttpStatus.OK)
  public User getUserById(@PathVariable("id") long id) {
    try {
      return userService.getById(id);
    } catch (EntityNotFoundException e1) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e1.getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  // -------------------- Users by username [USER/ADMIN] --------------------
  @GetMapping("/{username}")
  @ResponseStatus(HttpStatus.OK)
  public User getUserByUsername(@PathVariable("username") String username) {
    try {
      return userService.getByUsername(username);
    } catch (EntityNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e2.getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  // -------------------- Admin [ADMIN] --------------------
  @GetMapping("/admins")
  @ResponseStatus(HttpStatus.OK)
  public List<Admin> getAdmins() {
    try {
      return adminService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  // -------------------- Account Holder [ADMIN] --------------------
  @GetMapping("/account_holders")
  @ResponseStatus(HttpStatus.OK)
  public List<AccountHolder> getAccountHolders() {
    try {
      return accountHolderService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  // -------------------- Third Party [ADMIN] --------------------
  @GetMapping("/third_parties")
  @ResponseStatus(HttpStatus.OK)
  public List<ThirdParty> getAThirdParties() {
    try {
      return thirdPartyService.getAll();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }


  // ======================================== POST Methods ========================================
  // -------------------- New Admin [ADMIN] --------------------
  @PostMapping("/new_admin")
  @ResponseStatus(HttpStatus.CREATED)
  public void createNewAdmin(@RequestBody @Valid UserDTO admin) {
    try {
      adminService.newUser(admin);
    } catch (EntityExistsException e1) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, e1.getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  // -------------------- New Account Holder [PUBLIC] --------------------
  @PostMapping("/new")
  @ResponseStatus(HttpStatus.CREATED)
  public void createNewAccountHolder(@RequestBody @Valid UserAccountHolderDTO accountHolder) {
    try {
      accountHolderService.newUser(accountHolder);
    } catch (EntityExistsException e1) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, e1.getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  // -------------------- New Third Party [ADMIN] --------------------
  @PostMapping("/new_third_party")
  @ResponseStatus(HttpStatus.CREATED)
  public void createNewThirdParty(@RequestBody @Valid UserDTO thirdParty) {
    try {
      thirdPartyService.newUser(thirdParty);
    } catch (EntityExistsException e1) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, e1.getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

  }


  // ======================================== PATCH Methods ========================================
  // -------------------- Change Password [USER/ADMIN] --------------------
  @PatchMapping("/{username}/change_password")
  @ResponseStatus(HttpStatus.OK)
  public void editPassword(@PathVariable("username") String username, @RequestBody @Valid UserEditPasswordDTO userPassword) {
    try {
      userService.editPassword(username, userPassword);
    } catch (IllegalArgumentException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e1.getMessage());
    } catch (EntityNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e2.getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  // -------------------- Edit User [ADMIN] --------------------
  @PatchMapping("/edit/user/{username}")
  @ResponseStatus(HttpStatus.OK)
  public void editUser(@PathVariable("username") String username, @RequestBody @Valid UserEditDTO user) {
    try {
      userService.edit(username, user);
    } catch (IllegalArgumentException e1) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e1.getMessage());
    } catch (EntityNotFoundException e2) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e2.getMessage());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }


}
