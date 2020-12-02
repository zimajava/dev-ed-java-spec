package org.zipli.socknet.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zipli.socknet.repositories.modelsRepositories.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDetailsServiceImplTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void loadUserByUsername() {
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userRepository);
        userDetailsService.loadUserByUsername("");
    }
}