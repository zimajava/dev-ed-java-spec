package org.zipli.socknet.security.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zipli.socknet.models.User;
import org.zipli.socknet.repositories.modelsRepositories.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Slf4j
@ExtendWith(SpringExtension.class)
class UserDetailsServiceImplTest {

    private User user = new User(51,
            "asdsda@asdasd.sad",
            "dsadasdasd",
            "asddd",
            "dsaaaaa");

    @Test
    void loadUserByUsernamePass(@Autowired UserRepository userRepository) {

        userRepository.save(user);

        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userRepository);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());

        assertEquals(userDetails.getUsername(), user.getUserName());
    }

    @Test
    void loadUserByUsernameFail(@Autowired UserRepository userRepository) throws NullPointerException{

        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userRepository);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());

        try {
            userDetails.getUsername();
            fail("NullPointerException must be thrown");
        }catch (NullPointerException e){
            assertNull(e.getMessage());
        }
    }

}