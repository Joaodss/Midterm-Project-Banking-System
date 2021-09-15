package com.ironhack.midterm.repository.user;

import com.ironhack.midterm.dao.user.AccountHolder;
import com.ironhack.midterm.model.Address;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)   // Resets DB and id generation (slower)
class AccountHolderRepositoryTest {

  @Autowired
  private AccountHolderRepository accountHolderRepository;

  private Address pa1;
  private Address pa2;

  private AccountHolder ah1;
  private AccountHolder ah2;
  private AccountHolder ah3;

  @BeforeEach
  void setUp() {
    pa1 = new Address("Rua 1", "1010", "Coimbra", "Portugal");
    pa1 = new Address("Rua 244", "3526", "Porto", "Portugal");
    pa2 = new Address("Rua 22", "2222", "Lisbon", "Portugal");

    ah1 = new AccountHolder("joaodss", "123456", "João Afonso", LocalDate.parse("1996-10-01"), pa1, pa1);
    ah2 = new AccountHolder("anamaria", "123456", "Ana Maria", LocalDate.parse("1989-08-25"), pa2);
    ah3 = new AccountHolder("jose", "123456", "Jose", LocalDate.parse("1964-03-19"), pa1, pa2);
    accountHolderRepository.saveAll(List.of(ah1, ah2, ah3));
  }

  @AfterEach
  void tearDown() {
    accountHolderRepository.deleteAll();
  }


  // ======================================== CRUD TESTING ========================================
  @Test
  @Order(1)
  void testCount_numberOfAccountHoldersInDatabase_correctAmount() {
    assertEquals(3, accountHolderRepository.count());
  }

  // ==================== Create ====================
  @Test
  @Order(2)
  void testCreateAccountHolder_saveNewAccountHolderWithOneOwner_storedInRepository() {
    var initialSize = accountHolderRepository.count();
    accountHolderRepository.save(new AccountHolder("joaoa", "123456", "João Afonso", LocalDate.parse("1996-10-01"), pa2));
    assertEquals(initialSize + 1, accountHolderRepository.count());
  }

  // ==================== Read ====================
  @Test
  @Order(3)
  void testReadAccountHolder_findAll_returnsListOfObjectsNotEmpty() {
    var allElements = accountHolderRepository.findAll();
    assertFalse(allElements.isEmpty());
  }

  @Test
  @Order(3)
  void testReadAccountHolder_findById_returnsObjectsWithSameId() {
    var element1 = accountHolderRepository.findById(2L);
    if (element1.isPresent()) {
      assertEquals(2L, element1.get().getId());
    } else throw new TestInstantiationException("Id not found");
  }

  // ==================== Update ====================
  @Test
  @Order(4)
  void testUpdateAccountHolder_changeInterestRate_newInterestRateEqualsDefinedValue() {
    var element1 = accountHolderRepository.findById(3L);
    if (element1.isPresent()) {
      element1.get().setName("New name");
      accountHolderRepository.save(element1.get());
    } else throw new TestInstantiationException("Id not found");

    var updatedElement1 = accountHolderRepository.findById(3L);
    if (updatedElement1.isPresent()) {
      assertEquals("New name", updatedElement1.get().getName());
    } else throw new TestInstantiationException("Updated id not found");
  }

  // ==================== Delete ====================
  @Test
  @Order(5)
  void testDeleteAccountHolder_deleteAccountHolder_deletedFromRepository() {
    var initialSize = accountHolderRepository.count();
    accountHolderRepository.deleteById(2L);
    assertEquals(initialSize - 1, accountHolderRepository.count());
  }


}