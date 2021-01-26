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

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        user = new User("login@ukr.net", "Password5", "UserName5", "UserName5");
        user.setId("328717123122");
        user.setConfirm(true);

        userRepository.save(user);

        user2 = new User("lowwgin@ukr.net", "Passwwword5", "UsewwrName5", "UsewwrName5");
        user2.setConfirm(true);
        user2.setId("328717111123122");
        users.add(user2);

        user3 = new User("logdsin@ukr.net", "Passsdword5", "UserdsName5", "UsersdName5");
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
        assertEquals(userRepository.findUserByUserName(user.getUserName()).getId(), user.getId());
    }

    @Test
    void findUserByUserName_Fail() {
        assertNull(userRepository.findUserByUserName("wrong"));
    }

    @Test
    void deleteById_Pass() {
        userRepository.deleteById(user.getId());
        assertEquals(userRepository.findUserByUserName(user.getUserName()), null);
    }

    @Test
    void deleteById_Fail() {
        userRepository.deleteById(null);
        assertEquals(userRepository.findUserByUserName(user.getUserName()).getId(), user.getId());
    }

    @Test
    void updateOrDeleteAvatar_Pass() {
        userRepository.updateOrDeleteAvatar(user.getId(), null);
        assertEquals(userRepository.findUserByUserName(user.getUserName()).getAvatar(), null);

        userRepository.updateOrDeleteAvatar(user.getId(), "dsdddd");
        assertEquals(userRepository.findUserByUserName(user.getUserName()).getAvatar(), "dsdddd");
    }

    @Test
    void updateOrDeleteAvatar_Fail() {
        userRepository.updateOrDeleteAvatar(null, null);
        assertEquals(userRepository.findUserByUserName(user.getUserName()).getId(), user.getId());
    }

    @Test
    void updateNickName_Pass() {
        userRepository.updateNickName(user.getId(), "Nick");
        assertEquals(userRepository.findUserByUserName(user.getUserName()).getNickName(), "Nick");
    }

    @Test
    void updateNickName_Fail() {
        userRepository.updateNickName(null, null);
        assertEquals(userRepository.findUserByUserName(user.getUserName()).getId(), user.getId());
    }

    @Test
    void updateEmail_Pass() {
        userRepository.updateEmail(user.getId(), "Esochka@gmail.com");
        assertEquals(userRepository.findUserByUserName(user.getUserName()).getEmail(), "Esochka@gmail.com");
    }

    @Test
    void updateEmail_Fail() {
        userRepository.updateEmail(null, null);
        assertEquals(userRepository.findUserByUserName(user.getUserName()).getId(), user.getId());
    }

    @Test
    void updatePassword_Pass() {
        userRepository.updatePassword(user.getId(), "Parol5");
        assertEquals(userRepository.findUserByUserName(user.getUserName()).getPassword(), "Parol5");
    }

    @Test
    void updatePassword_Fail() {
        userRepository.updatePassword(null, null);
        assertEquals(userRepository.findUserByUserName(user.getUserName()).getId(), user.getId());
    }
}