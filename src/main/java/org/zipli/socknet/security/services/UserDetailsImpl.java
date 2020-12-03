package org.zipli.socknet.security.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.zipli.socknet.models.User;

import java.util.Collection;
import java.util.Objects;


public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;
    private User user;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getPassword(),
                user.getNickName(),
                user.isConfirm());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return  Objects.equals(this.user.getUserName(), user.getUsername())
                && Objects.equals(this.user.getPassword(),user.getPassword());
    }
}
