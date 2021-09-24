package com.ironhack.midterm.controller;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.dao.user.ThirdParty;
import com.ironhack.midterm.dao.user.User;
import com.ironhack.midterm.dto.*;

import java.util.List;

public interface UserController {

  // ======================================== GET Methods ========================================
  // -------------------- Global USER [ADMIN] --------------------
  List<User> getUsers();

  User getUserById(long id);

  User getUserByUsername(String username);

  // -------------------- Admins [ADMIN] --------------------
  List<Admin> getAdmins();

  // -------------------- Account Holder [ADMIN] --------------------
  List<AccountHolder> getAccountHolders();

  // -------------------- Third Parties [ADMIN] --------------------
  List<ThirdParty> getAThirdParties();


  // ======================================== POST Methods ========================================
  // -------------------- New Admin [ADMIN] --------------------
  void createNewAdmin(AdminDTO admin);

  // -------------------- New Account Holder [PUBLIC] --------------------
  void createNewAccountHolder(AccountHolderDTO accountHolder);

  // -------------------- New Third Party [ADMIN] --------------------
  void createNewThirdParty(ThirdPartyDTO thirdParty);


  // ======================================== PATCH Methods ========================================
  void editPassword(String username, UserPasswordDTO userPassword);

  void editUser(String username, UserDTO user);


  // ======================================== DELETE Methods ========================================
  void deleteUser(long id, String username, String password);

}
