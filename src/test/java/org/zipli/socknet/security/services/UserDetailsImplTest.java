package org.zipli.socknet.security.services;

import org.junit.jupiter.api.Test;
import org.zipli.socknet.models.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDetailsImplTest {

    private UserDetailsImpl userDetails = new UserDetailsImpl(
            new User(1,
                    "dsadasd",
                    "dsadsad",
                    "dasdasdasd",
                    "asd"));

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
        UserDetailsImpl userDetailsOne = new UserDetailsImpl(new User(1, "dsadasd", "dsadsad", "dasdasdasd", "asd"));
        UserDetailsImpl userDetailsTwo = new UserDetailsImpl(new User(1, "dsadasd", "dsadsad", "dasdasdasd", "asd"));

        assertTrue(userDetailsOne.equals(userDetailsTwo));
        assertTrue(userDetailsTwo.equals(userDetailsOne));

    }
}