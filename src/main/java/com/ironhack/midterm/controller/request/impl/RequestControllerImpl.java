//package com.ironhack.midterm.controller.request.impl;
//
//import com.ironhack.midterm.dao.request.Request;
//import com.ironhack.midterm.dto.NewAccountDTO;
//import com.ironhack.midterm.service.request.RequestNewService;
//import com.ironhack.midterm.service.request.RequestService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import javax.validation.Valid;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/request")
//public class RequestControllerImpl {
//
//  @Autowired
//  private RequestNewService requestNewService;
//
////  @Autowired
////  private RequestService requestService;
////  @Autowired
////  private RequestService requestService;
//
//  @GetMapping("")
//  @ResponseStatus(HttpStatus.OK)
//  public List<Request> getEmployees() {
//    try {
//      return requestNewService.getAll();
//    } catch (Exception e) {
//      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//  }
//
//  @PostMapping("/new_account")
//  @ResponseStatus(HttpStatus.CREATED)
//  public void createRequestNewAccount(@RequestBody @Valid NewAccountDTO newAccountDTO) {
//    try {
//      requestNewService.requestNew(newAccountDTO.getDescription(), newAccountDTO.getAccountType());
//    } catch (Exception e) {
//      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//  }
//
//
//}
