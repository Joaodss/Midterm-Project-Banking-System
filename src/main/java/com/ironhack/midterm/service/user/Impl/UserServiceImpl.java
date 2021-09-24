package com.ironhack.midterm.service.user.Impl;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.User;
import com.ironhack.midterm.dto.UserEditDTO;
import com.ironhack.midterm.dto.UserEditPasswordDTO;
import com.ironhack.midterm.repository.user.UserRepository;
import com.ironhack.midterm.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;


  // ======================================== GET Methods ========================================
  public List<User> getAll() {
    return userRepository.findAll();
  }

  public User getById(Long id) throws InstanceNotFoundException {
    var user = userRepository.findById(id);
    if (user.isPresent()) return user.get();
    throw new InstanceNotFoundException();
  }

  public User getByUsername(String username) throws InstanceNotFoundException {
    var user = userRepository.findByUsername(username);
    if (user.isPresent()) return user.get();
    throw new InstanceNotFoundException();
  }

  public boolean isUsernamePresent(String username) {
    return userRepository.findByUsername(username).isPresent();
  }


  public void changePassword(String username, UserEditPasswordDTO userPassword) throws InstanceNotFoundException {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    User user = getByUsername(username);

    if (!encoder.matches(userPassword.getCurrentPassword(), user.getPassword()))
      throw new IllegalArgumentException("Incorrect current password.");
    if (!userPassword.getNewPassword().equals(userPassword.getRepeatedNewPassword()))
      throw new IllegalArgumentException("Repeated passwords do not match.");

    user.setPassword(encoder.encode(userPassword.getNewPassword()));
    userRepository.save(user);
  }

  public void edit(String username, UserEditDTO userEditDTO) throws InstanceNotFoundException {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    User user = getByUsername(username);

    if (userEditDTO.getUsername() != null) user.setUsername(userEditDTO.getUsername());
    if (userEditDTO.getPassword() != null) user.setPassword(encoder.encode(userEditDTO.getPassword()));
    if (userEditDTO.getName() != null) user.setName(userEditDTO.getName());

    if (user.getClass() == AccountHolder.class && userEditDTO.getDateOfBirth() != null)
      ((AccountHolder) user).setDateOfBirth(userEditDTO.getDateOfBirth());

    if (user.getClass() == AccountHolder.class && userEditDTO.getPaStreetAddress() != null)
      ((AccountHolder) user).getPrimaryAddress().setStreetAddress(userEditDTO.getPaStreetAddress());
    if (user.getClass() == AccountHolder.class && userEditDTO.getPaPostalCode() != null)
      ((AccountHolder) user).getPrimaryAddress().setPostalCode(userEditDTO.getPaPostalCode());
    if (user.getClass() == AccountHolder.class && userEditDTO.getPaCity() != null)
      ((AccountHolder) user).getPrimaryAddress().setCity(userEditDTO.getPaCity());
    if (user.getClass() == AccountHolder.class && userEditDTO.getPaCountry() != null)
      ((AccountHolder) user).getPrimaryAddress().setCountry(userEditDTO.getPaCountry());

    if (((AccountHolder) user).getMailingAddress() != null) {

      if (user.getClass() == AccountHolder.class && userEditDTO.getMaStreetAddress() != null)
        ((AccountHolder) user).getMailingAddress().setStreetAddress(userEditDTO.getMaStreetAddress());
      if (user.getClass() == AccountHolder.class && userEditDTO.getMaPostalCode() != null)
        ((AccountHolder) user).getMailingAddress().setPostalCode(userEditDTO.getMaPostalCode());
      if (user.getClass() == AccountHolder.class && userEditDTO.getMaCity() != null)
        ((AccountHolder) user).getMailingAddress().setCity(userEditDTO.getMaCity());
      if (user.getClass() == AccountHolder.class && userEditDTO.getMaCountry() != null)
        ((AccountHolder) user).getMailingAddress().setCountry(userEditDTO.getMaCountry());

    } else if (((AccountHolder) user).getMailingAddress() == null &&
        userEditDTO.getMaStreetAddress() != null && userEditDTO.getMaPostalCode() != null &&
        userEditDTO.getMaCity() != null && userEditDTO.getMaCountry() != null) {

      ((AccountHolder) user).getMailingAddress().setStreetAddress(userEditDTO.getMaStreetAddress());
      ((AccountHolder) user).getMailingAddress().setPostalCode(userEditDTO.getMaPostalCode());
      ((AccountHolder) user).getMailingAddress().setCity(userEditDTO.getMaCity());
      ((AccountHolder) user).getMailingAddress().setCountry(userEditDTO.getMaCountry());
    }
    userRepository.save(user);
  }


}
