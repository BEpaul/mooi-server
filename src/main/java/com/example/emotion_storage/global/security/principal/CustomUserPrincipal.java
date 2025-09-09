package com.example.emotion_storage.global.security.principal;

import java.util.Collection;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserPrincipal(Long id, String email, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
