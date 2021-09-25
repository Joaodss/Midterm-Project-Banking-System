package com.ironhack.midterm.service.user.Impl;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.User;
import com.ironhack.midterm.dto.UserEditDTO;
import com.ironhack.midterm.dto.UserEditPasswordDTO;
import com.ironhack.midterm.model.Address;
import com.ironhack.midterm.repository.user.AccountHolderRepository;
import com.ironhack.midterm.repository.user.UserRepository;
import com.ironhack.midterm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AccountHolderRepository accountHolderRepository;


  // ======================================== get Methods ========================================
  public List<User> getAll() {
    return userRepository.findAll();
  }

  public User getById(Long id) throws EntityNotFoundException {
    var user = userRepository.findById(id);
    if (user.isPresent()) return user.get();
    throw new EntityNotFoundException();
  }

  public User getByUsername(String username) throws EntityNotFoundException {
    var user = userRepository.findByUsername(username);
    if (user.isPresent()) return user.get();
    throw new EntityNotFoundException();
  }


  // ======================================== edit Methods ========================================
  public void editPassword(String username, UserEditPasswordDTO userPassword) throws EntityNotFoundException, IllegalArgumentException {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    User user = getByUsername(username);
    // Check if current password is valid. Check if new passwords match.
    if (!encoder.matches(userPassword.getCurrentPassword(), user.getPassword()))
      throw new IllegalArgumentException("Incorrect current password.");
    if (!userPassword.getNewPassword().equals(userPassword.getRepeatedNewPassword()))
      throw new IllegalArgumentException("Repeated passwords do not match.");

    user.setPassword(encoder.encode(userPassword.getNewPassword()));
    userRepository.save(user);
  }

  public void edit(String username, UserEditDTO userEditDTO) throws EntityNotFoundException {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    User user = getByUsername(username);

    if (userEditDTO.getUsername() != null) {
      if (!isUsernamePresent(userEditDTO.getUsername())) {
        user.setUsername(userEditDTO.getUsername());
      } else throw new IllegalArgumentException("Username already exists.");
    }
    if (userEditDTO.getPassword() != null) user.setPassword(encoder.encode(userEditDTO.getPassword()));
    if (userEditDTO.getName() != null) user.setName(userEditDTO.getName());

    if (user.getClass() == AccountHolder.class) {
      AccountHolder accountHolderUser = (AccountHolder) user;

      if (userEditDTO.getDateOfBirth() != null)
        accountHolderUser.setDateOfBirth(userEditDTO.getDateOfBirth());
      // Edit Primary address properties
      if (userEditDTO.getPaStreetAddress() != null)
        accountHolderUser.getPrimaryAddress().setStreetAddress(userEditDTO.getPaStreetAddress());
      if (userEditDTO.getPaPostalCode() != null)
        accountHolderUser.getPrimaryAddress().setPostalCode(userEditDTO.getPaPostalCode());
      if (userEditDTO.getPaCity() != null)
        accountHolderUser.getPrimaryAddress().setCity(userEditDTO.getPaCity());
      if (userEditDTO.getPaCountry() != null)
        accountHolderUser.getPrimaryAddress().setCountry(userEditDTO.getPaCountry());

      if (accountHolderUser.getMailingAddress() != null) {
        // Edit Mailing address properties
        if (userEditDTO.getMaStreetAddress() != null)
          accountHolderUser.getMailingAddress().setStreetAddress(userEditDTO.getMaStreetAddress());
        if (userEditDTO.getMaPostalCode() != null)
          accountHolderUser.getMailingAddress().setPostalCode(userEditDTO.getMaPostalCode());
        if (userEditDTO.getMaCity() != null)
          accountHolderUser.getMailingAddress().setCity(userEditDTO.getMaCity());
        if (userEditDTO.getMaCountry() != null)
          accountHolderUser.getMailingAddress().setCountry(userEditDTO.getMaCountry());

      } else if (userEditDTO.getMaStreetAddress() != null &&
          userEditDTO.getMaPostalCode() != null &&
          userEditDTO.getMaCity() != null &&
          userEditDTO.getMaCountry() != null) {
        // Set new Mailing address properties
        accountHolderUser.setMailingAddress(
            new Address(
                userEditDTO.getMaStreetAddress(),
                userEditDTO.getMaPostalCode(),
                userEditDTO.getMaCity(),
                userEditDTO.getMaCountry()
            )
        );
      }
      accountHolderRepository.save(accountHolderUser);

    } else userRepository.save(user);
  }


  // ======================================== util Methods ========================================
  public boolean isUsernamePresent(String username) {
    return userRepository.findByUsername(username).isPresent();
  }

}
