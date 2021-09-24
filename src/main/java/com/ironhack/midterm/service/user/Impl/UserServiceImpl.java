package com.ironhack.midterm.service.user.Impl;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.dao.user.User;
import com.ironhack.midterm.dto.UserDTO;
import com.ironhack.midterm.dto.UserPasswordDTO;
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


  public void changePassword(String username, UserPasswordDTO userPassword) throws InstanceNotFoundException {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    User user = getByUsername(username);

    if (!encoder.matches(userPassword.getCurrentPassword(), user.getPassword()))
      throw new IllegalArgumentException("Incorrect current password.");
    if (!userPassword.getNewPassword().equals(userPassword.getRepeatedNewPassword()))
      throw new IllegalArgumentException("Repeated passwords do not match.");

    user.setPassword(userPassword.getNewPassword());
    userRepository.save(user);
  }

  public void edit(String username, UserDTO userDTO) throws InstanceNotFoundException {
    User user = getByUsername(username);

    if (userDTO.getUsername() != null) user.setUsername(userDTO.getUsername());
    if (userDTO.getPassword() != null) user.setPassword(userDTO.getPassword());
    if (userDTO.getName() != null) user.setName(userDTO.getName());

    if (user.getClass() == AccountHolder.class && userDTO.getDateOfBirth() != null)
      ((AccountHolder) user).setDateOfBirth(userDTO.getDateOfBirth());

    if (user.getClass() == AccountHolder.class && userDTO.getPaStreetAddress() != null)
      ((AccountHolder) user).getPrimaryAddress().setStreetAddress(userDTO.getPaStreetAddress());
    if (user.getClass() == AccountHolder.class && userDTO.getPaPostalCode() != null)
      ((AccountHolder) user).getPrimaryAddress().setPostalCode(userDTO.getPaPostalCode());
    if (user.getClass() == AccountHolder.class && userDTO.getPaCity() != null)
      ((AccountHolder) user).getPrimaryAddress().setCity(userDTO.getPaCity());
    if (user.getClass() == AccountHolder.class && userDTO.getPaCountry() != null)
      ((AccountHolder) user).getPrimaryAddress().setCountry(userDTO.getPaCountry());

    if (((AccountHolder) user).getMailingAddress() != null) {

      if (user.getClass() == AccountHolder.class && userDTO.getMaStreetAddress() != null)
        ((AccountHolder) user).getMailingAddress().setStreetAddress(userDTO.getMaStreetAddress());
      if (user.getClass() == AccountHolder.class && userDTO.getMaPostalCode() != null)
        ((AccountHolder) user).getMailingAddress().setPostalCode(userDTO.getMaPostalCode());
      if (user.getClass() == AccountHolder.class && userDTO.getMaCity() != null)
        ((AccountHolder) user).getMailingAddress().setCity(userDTO.getMaCity());
      if (user.getClass() == AccountHolder.class && userDTO.getMaCountry() != null)
        ((AccountHolder) user).getMailingAddress().setCountry(userDTO.getMaCountry());

    } else if (((AccountHolder) user).getMailingAddress() == null &&
        userDTO.getMaStreetAddress() != null && userDTO.getMaPostalCode() != null &&
        userDTO.getMaCity() != null && userDTO.getMaCountry() != null) {

      ((AccountHolder) user).getMailingAddress().setStreetAddress(userDTO.getMaStreetAddress());
      ((AccountHolder) user).getMailingAddress().setPostalCode(userDTO.getMaPostalCode());
      ((AccountHolder) user).getMailingAddress().setCity(userDTO.getMaCity());
      ((AccountHolder) user).getMailingAddress().setCountry(userDTO.getMaCountry());
    }
    userRepository.save(user);
  }

  public void delete(long id, String username, String password) throws InstanceNotFoundException {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    User user = getById(id);
    if (!user.getUsername().equals(username) || !encoder.matches(password, user.getPassword()))
      throw new IllegalArgumentException("User data does not match");

    userRepository.delete(user);
  }

}
