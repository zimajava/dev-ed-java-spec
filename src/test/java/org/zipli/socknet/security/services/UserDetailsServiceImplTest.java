package org.zipli.socknet.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.zipli.socknet.models.User;
import org.zipli.socknet.repositories.modelsRepositories.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserDetailsServiceImplTest {

    @Autowired
    UserRepository userRepository;

    private User user = new User(51,
            "asdsda@asdasd.sad",
            "dsadasdasd",
            "asddd",
            "dsaaaaa");

    @Test
    void loadUserByUsername() {
        userRepository.save(user);

        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userRepository);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());

        assertEquals(userDetails.getUsername(), user.getUserName());
        userRepository.deleteAllByUserName(user.getUserName());
    }

}