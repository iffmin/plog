package com.plog.PLOG.repository;

import com.plog.PLOG.entity.entity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface repository extends JpaRepository<entity, Long> {
    boolean existsByUsername(String username);

    Optional<entity> findByUsername(String username);

    @Transactional
    void deleteByUsername(String username);
}
