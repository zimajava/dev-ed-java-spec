package org.zipli.socknet.security.services;

import org.junit.jupiter.api.Test;
import org.zipli.socknet.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    private UserDetailsImpl userDetails = new UserDetailsImpl(new User(1,"dsadasd","dsadsad","dasdasdasd"));

    @Test
    void getPassword() {
        assertEquals(userDetails.getPassword(),"dasdasdasd");
    }

    @Test
    void getUsername() {
        assertEquals(userDetails.getUsername(),"dsadsad");
    }

    @Test
    void isAccountNonExpired() {
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked() {
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired() {
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void isEnabled() {
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void equals1() {
        UserDetailsImpl userDetailsOne = new UserDetailsImpl(new User(1,"dsadasd","dsadsad","dasdasdasd"));
        UserDetailsImpl userDetailsTwo = new UserDetailsImpl(new User(1,"dsadasd","dsadsad","dasdasdasd"));

        assertTrue(userDetailsOne.equals(userDetailsTwo));
        assertTrue(userDetailsTwo.equals(userDetailsOne));

    }
}