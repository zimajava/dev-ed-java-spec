package org.zipli.socknet.security.services;

import org.junit.jupiter.api.Test;
import org.zipli.socknet.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDetailsImplTest {

    private UserDetailsImpl userDetails = new UserDetailsImpl(
            new User("dsadasd",
                    "dsadsad",
                    "dasdasdasd",
                    "dsad"));

    @Test
    void getPassword() {
        assertEquals(userDetails.getPassword(), "dsadsad");
    }

    @Test
    void getUsername() {
        assertEquals(userDetails.getUsername(), "dasdasdasd");
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
        UserDetailsImpl userDetailsOne = new UserDetailsImpl(
                new User("dsadasd",
                        "dsadsad",
                        "dasdasdasd",
                        "sas"));
        UserDetailsImpl userDetailsTwo = new UserDetailsImpl(
                new User("dsadasd",
                        "dsadsad",
                        "dasdasdasd",
                        "saa"));

        assertTrue(userDetailsOne.equals(userDetailsTwo));
        assertTrue(userDetailsTwo.equals(userDetailsOne));
    }
}