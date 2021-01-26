package org.zipli.socknet.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zipli.socknet.repository.model.User;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class UserRepositoryTest {

    private User user;
    private User user2;
    private User user3;
    private List<User> users = new ArrayList<>();
    private Collection<String> userId = new ArrayList<>();
    private String param;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        user = new User("StudyEmail@ukr.net", "asf2431", "SamS", "SSY");
        user.setId("328717123122");
        user.setConfirm(true);

        userRepository.save(user);

        user2 = new User("Saemail@ukr.net", "qwerty1234", "Smoky", "BestFriend");
        user2.setConfirm(true);
        user2.setId("328717111123122");
        users.add(user2);

        user3 = new User("homeEmail@ukr.net", "uyy234", "HomeAccount", "SaMie");
        user3.setConfirm(true);
        user3.setId("328717123333122");
        users.add(user3);

        userId.add(user.getId());
        userId.add(user2.getId());
        userId.add(user3.getId());
        userRepository.saveAll(users);
    }

    @Test
    void save_Pass() {
        assertEquals(userRepository.getUserById(user.getId()).getId(), user.getId());
    }

    @Test
    void save_Fail() {
        assertThrows(IllegalArgumentException.class, () -> {
            userRepository.save(null);
        });
    }

    @Test
    void saveAll_Pass() {
        assertEquals(userRepository.getUserById(user2.getId()).getId(), user2.getId());
        assertEquals(userRepository.getUserById(user3.getId()).getId(), user3.getId());
    }

    @Test
    void saveAll_Fail() {
        assertThrows(NullPointerException.class, () -> {
            userRepository.saveAll(null);
        });
    }

    @Test
    void getUserByEmail_Pass() {
        assertEquals(userRepository.getUserByEmail(user.getEmail()).getId(), user.getId());
    }

    @Test
    void getUserByEmail_Fail() {
        assertNull(userRepository.getUserByEmail("wrong"));
    }

    @Test
    void getUserByUserName_Pass() {
        assertEquals(userRepository.getUserByUserName(user.getUserName()).getId(), user.getId());
    }

    @Test
    void getUserByUserName_Fail() {
        assertNull(userRepository.getUserByUserName("wrong"));
    }

    @Test
    void getUserById_Pass() {
        assertEquals(userRepository.getUserById(user.getId()).getId(), user.getId());
    }

    @Test
    void getUserById_Fail() {
        assertNull(userRepository.getUserById("wrong"));
    }

    @Test
    void findUsersByIdIn_Pass() {

        assertEquals(userRepository.findUsersByIdIn(userId).size(), 3);
    }

    @Test
    void findUsersByIdIn_Fail() {
        assertNull(userRepository.getUserById("wrong"));
    }

    @Test
    void findAllByIsConfirm_Pass() {
        assertEquals(userRepository.findAllByIsConfirm(true).size(), 3);
    }

    @Test
    void findAllByIsConfirm_Fail() {
        assertFalse(userRepository.findAllByIsConfirm(false).containsAll(users));
    }

    @Test
    void findUserByEmail_Pass() {
        assertEquals(userRepository.findUserByEmail(user.getEmail()).getId(), user.getId());
    }

    @Test
    void findUserByEmail_Fail() {
        assertNull(userRepository.findUserByEmail("wrong"));
    }

    @Test
    void findUserByUserName_Pass() {
        assertEquals(userRepository.getUserByUserName(user.getUserName()).getId(), user.getId());
    }

    @Test
    void findUserByUserName_Fail() {
        assertNull(userRepository.getUserByUserName("wrong"));
    }

    @Test
    void deleteById_Pass() {
        userRepository.deleteById(user.getId());
        assertEquals(userRepository.getUserByUserName(user.getUserName()), null);
    }

    @Test
    void deleteById_Fail() {
        userRepository.deleteById(null);
        assertEquals(userRepository.getUserByUserName(user.getUserName()).getId(), user.getId());
    }

    @Test
    void findUsersBySearchParam_Match_In_3_Fields() {
        List<User> expectedList = new ArrayList<>();
        expectedList.add(user);
        expectedList.add(user2);
        expectedList.add(user3);

        param = "Sa";

        List<User> actualList = userRepository.findUsersBySearchParam(param);

        assertEquals(expectedList, actualList);
    }

    @Test
    void findUsersBySearchParam_Match_In_1_Field() {
        List<User> expectedList = new ArrayList<>();
        expectedList.add(user);

        param = "Study";

        List<User> actualList = userRepository.findUsersBySearchParam(param);

        assertEquals(expectedList, actualList);
    }

    @Test
    void findUsersBySearchParam_No_Match() {
        List<User> expectedList = new ArrayList<>();

        param = "Av";

        List<User> actualList = userRepository.findUsersBySearchParam(param);

        assertEquals(expectedList, actualList);
    }

}