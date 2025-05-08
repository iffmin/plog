package com.plog.PLOG.service;

import com.plog.PLOG.dto.CustomUserDetails;
import com.plog.PLOG.entity.entity;
import com.plog.PLOG.repository.repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private repository r;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        entity data = r.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        return new CustomUserDetails(data);
    }
}

