package com.plog.PLOG.controller;

import com.plog.PLOG.dto.BoardDTO;
import com.plog.PLOG.dto.LocationDTO;
import com.plog.PLOG.entity.BoardEntity;
import com.plog.PLOG.entity.LocationEntity;
import com.plog.PLOG.repository.BoardRepository;
import com.plog.PLOG.repository.LocationRepository;
import com.plog.PLOG.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping
public class search_controller {

    private final BoardService boardService;
    private final BoardRepository boardRepository;

    public search_controller(BoardService boardService, BoardRepository boardRepository) {
        this.boardService = boardService;
        this.boardRepository = boardRepository;
    }


    @GetMapping("/nearby")
    public ResponseEntity<List<LocationDTO>> getNearbyBoards(
            @RequestParam("lat") double latitude,
            @RequestParam("lng") double longitude) {

        double radius = 0.5;
        List<LocationDTO> nearbyBoards = boardService.findBoardsNearby(latitude, longitude, radius);

        for (LocationDTO dto : nearbyBoards) {
            Long boardId = dto.getBoard_id();
            BoardEntity b_e = boardRepository.findById(boardId).orElseThrow();
            dto.setBoard_name(b_e.getTitle());
        }

        return ResponseEntity.ok(nearbyBoards);
    }

    @GetMapping("/read-all")
    public ResponseEntity<List<BoardDTO>> getallboards(){
        List<BoardDTO> BOARDLIST = boardService.readallBoards();
        return ResponseEntity.ok(BOARDLIST);
    }

    @GetMapping("/api/coordinates")
    public ResponseEntity<Map<String, Double>> getCoordinates(@RequestParam Long id) {
        Map<String, Double> location = boardService.findLocation(id);

        return ResponseEntity.ok(location);
    }



}
