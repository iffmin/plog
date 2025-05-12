package com.plog.PLOG.dto;

import com.plog.PLOG.entity.entity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private entity userdata;

    public CustomUserDetails(entity userdata){

        this.userdata = userdata;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userdata.getRole().toString()));
    }

    @Override
    public String getPassword() {
        return userdata.getPassword();
    }

    @Override
    public String getUsername() {
        return userdata.getUsername();
    }

    public Long getId(){
        return userdata.getId();
    }

    public String getName(){
        return userdata.getName();
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
}
