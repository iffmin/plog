package com.plog.PLOG.service;

import com.plog.PLOG.dto.BoardDTO;
import com.plog.PLOG.entity.BoardEntity;
import com.plog.PLOG.entity.entity;
import com.plog.PLOG.repository.BoardRepository;
import com.plog.PLOG.repository.repository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final repository userRepository;

    public BoardService(BoardRepository boardRepository, repository userRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    public Boolean isAccess(Long id) {

        String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String sessionRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();

        if ("ROLE_ADMIN".equals(sessionRole)) {
            return true;
        }

        String boardUsername = boardRepository.findById(id).orElseThrow().getEntity().getUsername();

        if (sessionUsername.equals(boardUsername)) {
            return true;
        }
        return false;
    }

    @Transactional
    public void createOneBoard(BoardDTO dto){
        BoardEntity board = new BoardEntity();
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());

        boardRepository.save(board);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        entity user = userRepository.findByUsername(username).orElseThrow();

        user.addBoardEntity(board);
        userRepository.save(user);
    }

    @Transactional
    public BoardDTO readOneBoard(Long id){
        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow();

        BoardDTO dto = new BoardDTO();
        dto.setId(boardEntity.getId());
        dto.setTitle(boardEntity.getTitle());
        dto.setContent(boardEntity.getContent());

        return dto;
    }

    @Transactional
    public List<BoardDTO> readAllBoards() {

        List<BoardEntity> list = boardRepository.findAll();

        List<BoardDTO> dtos = new ArrayList<>();
        for (BoardEntity boardEntity : list) {
            BoardDTO dto = new BoardDTO();
            dto.setId(boardEntity.getId());
            dto.setTitle(boardEntity.getTitle());
            dto.setContent(boardEntity.getContent());

            dtos.add(dto);
        }

        return dtos;
    }

    @Transactional
    public void updateOneBoard(Long id, BoardDTO dto) {

        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow();


        boardEntity.setTitle(dto.getTitle());
        boardEntity.setContent(dto.getContent());

        boardRepository.save(boardEntity);
    }

    @Transactional
    public void deleteOneBoard(Long id) {

        boardRepository.deleteById(id);
    }


}

