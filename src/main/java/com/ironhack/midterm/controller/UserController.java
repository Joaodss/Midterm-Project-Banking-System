package com.ironhack.midterm.controller;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.dao.user.ThirdParty;
import com.ironhack.midterm.dao.user.User;
import com.ironhack.midterm.dto.UserAccountHolderDTO;
import com.ironhack.midterm.dto.UserDTO;
import com.ironhack.midterm.dto.UserEditDTO;
import com.ironhack.midterm.dto.UserEditPasswordDTO;

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
  void createNewAdmin(UserDTO admin);

  // -------------------- New Account Holder [PUBLIC] --------------------
  void createNewAccountHolder(UserAccountHolderDTO accountHolder);

  // -------------------- New Third Party [ADMIN] --------------------
  void createNewThirdParty(UserDTO thirdParty);


  // ======================================== PATCH Methods ========================================
  void editPassword(String username, UserEditPasswordDTO userPassword);

  void editUser(String username, UserEditDTO user);


}
