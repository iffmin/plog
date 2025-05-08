package com.plog.PLOG.service;

import com.plog.PLOG.dto.dto;
import com.plog.PLOG.entity.UserRoleType;
import com.plog.PLOG.entity.entity;
import com.plog.PLOG.repository.repository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class service {

    private final repository repo;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public service(repository repo, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.repo = repo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void toEntity(dto d){

        boolean isUser = repo.existsByUsername(d.getUsername());
        if(isUser){
            return;
        }

        entity data = new entity();

        data.setId(null);
        data.setName(d.getName());
        data.setUsername(d.getUsername());
        data.setPassword(bCryptPasswordEncoder.encode(d.getPassword()));
        data.setEmail(d.getEmail());

        data.setRole(UserRoleType.USER);
        repo.save(data);
    }

    public Boolean isAccess(String username){
        String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String sessionRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();

        if("ROLE_ADMIN".equals(sessionRole)){
            return true;
        }

        return username.equals(sessionUsername);

    }


    @Transactional
    public dto readOneUser(String username){
        Optional<entity> optionalEntity = repo.findByUsername(username);
        if (optionalEntity.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        entity data = optionalEntity.get();

        dto d = new dto();
        d.setName(data.getName());
        d.setUsername(data.getUsername());
        d.setPassword(data.getPassword());
        d.setEmail(data.getEmail());
        d.setRole(data.getRole().toString());

        return d;
    }

    @Transactional
    public List<dto> readAllUsers() {

        List<entity> list = repo.findAll();

        List<dto> dtos = new ArrayList<>();
        for (entity user : list) {
            dto d = new dto();
            d.setName(user.getName());
            d.setUsername(user.getUsername());
            d.setPassword(user.getPassword());
            d.setEmail(user.getEmail());
            d.setRole(user.getRole().toString());
            dtos.add(d);
        }

        return dtos;
    }

    @Transactional
    public void updateOneUser(dto dto, String username) {

        entity entity = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 없습니다."));

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        }


        if (dto.getName() != null && !dto.getName().isEmpty()) {
            entity.setName(dto.getName());
        }


        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            entity.setEmail(dto.getEmail());
        }

        repo.save(entity);
    }

    @Transactional
    public void deleteOneUser(String username){
        repo.deleteByUsername(username);
    }


}
