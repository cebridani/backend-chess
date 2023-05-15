package io.github.cebridani.chesswebapi.controller;

import java.util.List;
import java.util.Optional;

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
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.cebridani.chesswebapi.payload.Move;
import io.github.cebridani.chesswebapi.service.ChessService;

@RestController
@RequestMapping("/api/chess")
public class ChessController {

    private final ChessService chessService;

    public ChessController(ChessService chessService) {
        this.chessService = chessService;
    }
    
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/get_best_move")
    public ResponseEntity<String> sendFen(@RequestBody Move move, @RequestHeader("Authorization") String token) throws JsonMappingException, JsonProcessingException {
        System.out.println(move.toString());
    	String bestMove = chessService.processFen(move, token);
        return ResponseEntity.ok(bestMove);
    }
    
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/get_fen")
    public ResponseEntity<Move> getFen(@RequestHeader("Authorization") String token) {
        System.out.println("get Fen");
        Move fenByUser = chessService.getFenByUser(token);
        return ResponseEntity.ok(fenByUser);
    }
    
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/set_fen")
    public ResponseEntity<String> setFen(@RequestBody String body, @RequestHeader("Authorization") String token) {
    	ObjectMapper mapper = new ObjectMapper();
        Move move = null;
		try {
			move = mapper.readValue(body, Move.class);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(move.toString());
        System.out.println("set Fen");
        System.out.println(move.toString());
        chessService.setFenByUser(move, token);
        return ResponseEntity.ok("Fen setted");
    }
    
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/top_moves")
    public ResponseEntity<List<String>> top_moves(@RequestBody String fen, @RequestHeader("Authorization") String token) throws JsonMappingException, JsonProcessingException {
    	System.out.println("top moves");
        List<String> response = chessService.best_moves(fen, token);
        System.out.println("Best moves: "+response);
        return ResponseEntity.ok(response);
    }
    
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/get_evaluation")
    public ResponseEntity<String> get_evaluation(@RequestBody String fen, @RequestHeader("Authorization") String token) throws JsonMappingException, JsonProcessingException {
    	System.out.println("evaluation");
        String response = chessService.get_eval(fen, token);
        System.out.println("Evaluation: "+response);
        return ResponseEntity.ok(response);
    }
    
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/get_stadistics")
    public ResponseEntity<String> get_stadistics(@RequestBody String fen, @RequestHeader("Authorization") String token) throws JsonMappingException, JsonProcessingException {
        String response = chessService.get_stadistics(fen, token);
        return ResponseEntity.ok(response);
    }
    
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/get_pgn")
    public ResponseEntity<String> get_pgn(@RequestHeader("Authorization") String token) throws JsonMappingException, JsonProcessingException {
        String response = chessService.getPgnByUser(token);
        System.out.println("pgn: "+ response);
        return ResponseEntity.ok(response);
    }
    
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/set_pgn")
    public ResponseEntity<String> set_pgn(@RequestBody Optional<String> pgn, @RequestHeader("Authorization") String token) throws JsonMappingException, JsonProcessingException {
        chessService.savePgnByUser(token, pgn.orElse(""));
        return ResponseEntity.ok("Pgn setted");
    }

}

