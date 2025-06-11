package com.plog.PLOG.service;


import com.plog.PLOG.dto.BoardDTO;
import com.plog.PLOG.dto.LocationDTO;
import com.plog.PLOG.entity.BoardEntity;
import com.plog.PLOG.entity.LocationEntity;
import com.plog.PLOG.entity.entity;
import com.plog.PLOG.repository.BoardRepository;
import com.plog.PLOG.repository.LocationRepository;
import com.plog.PLOG.repository.repository;
import jakarta.persistence.Column;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            location.setLocationAddress(dto.getLocationAddress());
            location.setContent(dto.getContent());
            location.setLongitude(dto.getLongitude());
            location.setLatitude(dto.getLatitude());
            board.addlocationEntity(location);
            locationRepository.save(location);

        }
    }

    @Transactional
    public List<BoardDTO> readOneALLBoard(Long id){

        List<BoardEntity> list = boardRepository.findByEntityId(id, Sort.by(Sort.Direction.DESC, "id"));
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
            dto.setLocationAddress(locationEntity.getLocationAddress());
            dto.setContent(locationEntity.getContent());
            dto.setLatitude(locationEntity.getLatitude());
            dto.setLongitude(locationEntity.getLongitude());
            dto.setBoard_id(locationEntity.getBoardEntity().getId());

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
                entity.setLocationAddress(dto.getLocationAddress());
                entity.setContent(dto.getContent());
                entity.setLatitude(dto.getLatitude());
                entity.setLongitude(dto.getLongitude());
            } else {
                LocationEntity newEntity = new LocationEntity();
                newEntity.setLocationName(dto.getLocationName());
                newEntity.setLocationAddress(dto.getLocationAddress());
                newEntity.setContent(dto.getContent());
                newEntity.setLatitude(dto.getLatitude());
                newEntity.setLongitude(dto.getLongitude());
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
                locationDTO.setId(location.getId());
                locationDTO.setLocationName(location.getLocationName());
                locationDTO.setLocationAddress(location.getLocationAddress());
                locationDTO.setContent(location.getContent());
                locationDTO.setLatitude(location.getLatitude());
                locationDTO.setLongitude(location.getLongitude());
                locationDTO.setBoard_id(board.getId());
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

    @Transactional(readOnly = true)
    public List<LocationDTO> findBoardsNearby(double latitude, double longitude, double radius) {

        // 대략적 범위 쿼리
        List<LocationEntity> candidates = locationRepository.findByLatitudeBetweenAndLongitudeBetween(
                latitude - radius, latitude + radius,
                longitude - radius, longitude + radius
        );

        List<LocationDTO> dtos = new ArrayList<>();
        for (LocationEntity loc : candidates) {
            double distance = calculateDistance(latitude, longitude, loc.getLatitude(), loc.getLongitude());
            if (distance <= radius) {
                LocationDTO dto = new LocationDTO();
                dto.setId(loc.getId());
                dto.setBoard_id(loc.getBoardEntity().getId());
                dto.setLocationName(loc.getLocationName());
                dto.setLocationAddress(loc.getLocationAddress());
                dto.setContent(loc.getContent());
                dto.setLatitude(loc.getLatitude());
                dto.setLongitude(loc.getLongitude());

                dtos.add(dto);
            }
        }
        return dtos;
    }

    // Haversine 공식으로 두 좌표 거리(km) 계산
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // 지구 반경(km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    @Transactional
    public Map<String, Double> findLocation(Long id){ // id = locationid
        LocationEntity locationEntity = locationRepository.findById(id).orElseThrow();
        Map<String, Double> response = new HashMap<>();
        response.put("longitude", locationEntity.getLongitude());
        response.put("latitude", locationEntity.getLatitude());
        return response;
    }

}



