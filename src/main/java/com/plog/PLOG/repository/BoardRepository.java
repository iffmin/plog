package com.plog.PLOG.repository;

import com.plog.PLOG.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {


}