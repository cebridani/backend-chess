package io.github.cebridani.chesswebapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.github.cebridani.chesswebapi.payload.FenDto;
import io.github.cebridani.chesswebapi.service.ChessService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/chess")
public class ChessController {

    private final ChessService chessService;

    public ChessController(ChessService chessService) {
        this.chessService = chessService;
    }

    @PostMapping("/get_best_move")
    public ResponseEntity<String> sendFen(@RequestBody FenDto fenDto, @RequestHeader("Authorization") String token) throws JsonMappingException, JsonProcessingException {
        String bestMove = chessService.processFen(fenDto, token);
        return ResponseEntity.ok(bestMove);
    }
    
    @GetMapping("/get_fen")
    public ResponseEntity<FenDto> getFen(@RequestHeader("Authorization") String token) {
        FenDto fenByUser = chessService.getFenByUser(token);
        return ResponseEntity.ok(fenByUser);
    }
}

