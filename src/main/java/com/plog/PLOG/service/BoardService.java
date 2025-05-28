package com.plog.PLOG.service;


import com.plog.PLOG.dto.BoardDTO;
import com.plog.PLOG.dto.LocationDTO;
import com.plog.PLOG.entity.BoardEntity;
import com.plog.PLOG.entity.LocationEntity;
import com.plog.PLOG.entity.entity;
import com.plog.PLOG.repository.BoardRepository;
import com.plog.PLOG.repository.LocationRepository;
import com.plog.PLOG.repository.repository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final repository userRepository;
    private final LocationRepository locationRepository;
    public BoardService(BoardRepository boardRepository, repository userRepository, LocationRepository locationRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
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
    public void createOneBoard(BoardDTO b_dto, List<LocationDTO> l_dto) {
        BoardEntity board = new BoardEntity();

        board.setTitle(b_dto.getTitle());
        boardRepository.save(board);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        entity user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        user.addBoardEntity(board);
        userRepository.save(user);

        for (LocationDTO dto : l_dto) {
            LocationEntity location = new LocationEntity();
            location.setLocationName(dto.getLocationName());
            location.setContent(dto.getContent());
            location.setLongitude(dto.getLongitude());
            location.setLatitude(dto.getLatitude());
            board.addlocationEntity(location);
            locationRepository.save(location);

        }
    }

    @Transactional
    public List<BoardDTO> readOneALLBoard(Long id){

        List<BoardEntity> list = boardRepository.findByEntityId(id);
        List<BoardDTO> dtos = new ArrayList<>();

        for (BoardEntity boardEntity : list) {
            BoardDTO dto = new BoardDTO();
            dto.setId(boardEntity.getId());
            dto.setTitle(boardEntity.getTitle());

            dtos.add(dto);
        }

        return dtos;
    }
    @Transactional
    public List<LocationDTO> readBoard(Long id){

        List<LocationEntity> list = locationRepository.findByBoardEntityId(id);
        List<LocationDTO> dtos = new ArrayList<>();

        for (LocationEntity locationEntity : list) {
            LocationDTO dto = new LocationDTO();
            dto.setId(locationEntity.getId());
            dto.setLocationName(locationEntity.getLocationName());
            dto.setContent(locationEntity.getContent());

            dtos.add(dto);
        }

        return dtos;
    }

    @Transactional
    public List<LocationDTO> updateBoard(Long id, List<LocationDTO> dtos){
        BoardEntity board = boardRepository.findById(id).orElseThrow();

        List<LocationEntity> existingLocations = locationRepository.findByBoardEntityId(id);

        int existingSize = existingLocations.size();

        for (int i = 0; i < dtos.size(); i++) {
            LocationDTO dto = dtos.get(i);

            if (i < existingSize) {
                LocationEntity entity = existingLocations.get(i);
                entity.setLocationName(dto.getLocationName());
                entity.setContent(dto.getContent());
            } else {
                LocationEntity newEntity = new LocationEntity();
                newEntity.setLocationName(dto.getLocationName());
                newEntity.setContent(dto.getContent());
                newEntity.setBoardEntity(board);
                locationRepository.save(newEntity);
            }
        }

        return dtos;
    }

    @Transactional
    public List<BoardDTO> readallBoards() {

        List<BoardEntity> entities = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<BoardDTO> result = new ArrayList<>();
        for (BoardEntity board : entities) {
            BoardDTO boardDTO = new BoardDTO();
            boardDTO.setId(board.getId());
            boardDTO.setTitle(board.getTitle());

            List<LocationDTO> locationDTOList = new ArrayList<>();
            for (LocationEntity location : board.getLocationEntityList()) {
                LocationDTO locationDTO = new LocationDTO();
                locationDTO.setLocationName(location.getLocationName());
                locationDTO.setContent(location.getContent());
                locationDTOList.add(locationDTO);
            }

            boardDTO.setLocations(locationDTOList);
            result.add(boardDTO);
        }

        return result;
    }

    @Transactional
    public void deletelocation(Long id){
        locationRepository.deleteById(id); //location id
    }

    @Transactional
    public void deleteboard(Long id){ //board id
        List<LocationEntity> location = locationRepository.findByBoardEntityId(id);
        locationRepository.deleteAll(location);//board id
        boardRepository.deleteById(id);

    }


}



