package com.plog.PLOG.repository;

import com.plog.PLOG.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    List<BoardEntity> findByEntityId(Long entityId);


}