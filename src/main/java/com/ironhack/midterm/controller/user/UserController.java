package com.ironhack.midterm.controller.user;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.Admin;
import com.ironhack.midterm.dao.user.ThirdParty;
import com.ironhack.midterm.dao.user.User;
import com.ironhack.midterm.dto.AccountHolderDTO;
import com.ironhack.midterm.dto.AdminDTO;
import com.ironhack.midterm.dto.ThirdPartyDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UserController {

  // ======================================== GET Methods ========================================
  // -------------------- Global USER --------------------
  List<User> getUsers();

  User getUserById(long id);

  User getUserByUsername(@RequestParam("username") String username);

  // -------------------- Admins --------------------
  List<Admin> getAdmins();

  // -------------------- Account Holder --------------------
  List<AccountHolder> getAccountHolders();

  // -------------------- Third Parties --------------------
  List<ThirdParty> getAThirdParties();


  // ======================================== POST Methods ========================================


  // ======================================== PUT Methods ========================================
  // -------------------- New Admin [ADMIN] --------------------
  void createNewAdmin(AdminDTO admin);

  // -------------------- New Account Holder [PUBLIC] --------------------
  void createNewAccountHolder(AccountHolderDTO accountHolder);

  // -------------------- New Third Party [ADMIN] --------------------
  void createNewThirdParty(ThirdPartyDTO thirdParty);


  // ======================================== PATCH Methods ========================================


  // ======================================== DELETE Methods ========================================


}
