//package com.ironhack.midterm.service.request;
//
//import com.ironhack.midterm.dao.account.Account;
//import com.ironhack.midterm.dao.request.Request;
//import com.ironhack.midterm.enums.AccountType;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface RequestService {
//
//  // ======================================== GET Methods ========================================
//  List<Request> getAll();     // ADMIN
//
////  List<Request> getAllByType(Request request);      // ADMIN
//
//  Optional<Request> getById(long id);      // ADMIN
//
//
//  List<Request> getAll(Account associatedAccount);     // USER ADMIN
//
////  List<Request> getAllByType(Account associatedAccount, Request request);     // USER ADMIN
//
//  Optional<Request> getById(Account associatedAccount, long id);     // USER ADMIN
//
//
//  // ======================================== POST Methods ========================================
//
//  // ======================================== PUT Methods ========================================
//
//  // ======================================== PATCH Methods ========================================
//  void validateRequest(long id);      // ADMIN
//
//
//  // ======================================== DELETE Methods ========================================
//
//
//}
