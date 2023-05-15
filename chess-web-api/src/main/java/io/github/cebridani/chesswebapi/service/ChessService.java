package io.github.cebridani.chesswebapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.cebridani.chesswebapi.entity.User;
import io.github.cebridani.chesswebapi.payload.Move;
import io.github.cebridani.chesswebapi.payload.Pgn;
import io.github.cebridani.chesswebapi.repository.PgnRepository;
import io.github.cebridani.chesswebapi.repository.UserRepository;
import io.github.cebridani.chesswebapi.security.JwtTokenProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class ChessService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PgnRepository pgnRepository;

    public ChessService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, PgnRepository pgnRepository) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.pgnRepository = pgnRepository;
    }

    public String processFen(Move move, String token) throws JsonMappingException, JsonProcessingException {        
        
        String email = jwtTokenProvider.getUsername(token.substring(7));
        Optional<User> user = userRepository.findByEmail(email);

        StockfishService stockfish = new StockfishService();
        String stockfishPath = "/usr/games/stockfish";
        
        String bestMove = "";
	    
	if (stockfish.startEngine(stockfishPath)) {
		
	    System.out.println("Stockfish engine started successfully");
		bestMove = stockfish.getBestMove(move.getAfter());
		System.out.println(bestMove);
		stockfish.stopEngine();

	} else {
            System.out.println("Failed to start Stockfish engine");
    }
	    
		return bestMove;
    }

    public Move getFenByUser(String token) {
	String email = jwtTokenProvider.getUsername(token.substring(7));
	System.out.println("User: "+ email);
        Optional<User> user = userRepository.findByEmail(email);

        return new Move(user.get().getFen()); 
    }

    public void setFenByUser(Move move, String token) {
    	
    	String email = jwtTokenProvider.getUsername(token.substring(7));
		System.out.println("User: "+ email);
        Optional<User> user = userRepository.findByEmail(email);
        
        String newFen = move.getAfter();

        user.get().setFen(newFen);
        userRepository.save(user.get());         
    }
    
    public void savePgnByUser(String token, String pgnString) {

        String email = jwtTokenProvider.getUsername(token.substring(7));
        System.out.println("User: "+ email);
        Optional<User> userOpt = userRepository.findByEmail(email);

        if(userOpt.isPresent()) {
            User user = userOpt.get();
            Optional<Pgn> existingPgnOpt = pgnRepository.findByUser(user);

            Pgn pgn;
            if(existingPgnOpt.isPresent()){
                // Si existe un PGN para este usuario, actualiza el PGN
                pgn = existingPgnOpt.get();
                pgnString = pgnString.replace('+', ' ');
                if(pgnString.endsWith("=")) {
                    pgnString = pgnString.substring(0, pgnString.length() - 1);
                }
                pgn.setPgn(pgnString);
            } else {
                // Si no existe un PGN para este usuario, crea uno nuevo
                pgn = new Pgn();
                pgn.setUser(user);
                pgnString = pgnString.replace('+', ' ');
                if(pgnString.endsWith("=")) {
                    pgnString = pgnString.substring(0, pgnString.length() - 1);
                }
                pgn.setPgn(pgnString);
            }
            // Guarda el Pgn en la base de datos
            pgnRepository.save(pgn);
        } else {
            // Maneja el caso en el que no se encuentra el usuario
            System.out.println("User not found: " + email);
        }
    }


    
    public String getPgnByUser(String token) {
        // Encuentra el usuario
    	String email = jwtTokenProvider.getUsername(token.substring(7));
    	System.out.println("User: "+ email);
        Optional<User> user = userRepository.findByEmail(email);

        // Encuentra el Pgn para ese usuario
        Pgn pgn = pgnRepository.findByUser(user.get())
                .orElseThrow(() -> new RuntimeException("Pgn not found for user"));

        // Devuelve el string del Pgn
        return pgn.getPgn();
    }
	
    public List<String> best_moves(String fen, String token) throws JsonMappingException, JsonProcessingException {        
        
        String email = jwtTokenProvider.getUsername(token.substring(7));
        Optional<User> user = userRepository.findByEmail(email);

        StockfishService stockfish = new StockfishService();
        String stockfishPath = "/usr/games/stockfish";
        
        List<String> bestMoves = new ArrayList<>();
        
        if (stockfish.startEngine(stockfishPath)) {
            System.out.println("Stockfish engine started successfully");

            bestMoves = stockfish.getBestMoves(fen);
            System.out.println(bestMoves);

            stockfish.stopEngine();
        } else {
            System.out.println("Failed to start Stockfish engine");
        }
        
        return bestMoves;
    }
    
    public String get_eval(String fen, String token) {
    	String email = jwtTokenProvider.getUsername(token.substring(7));
    	System.out.println("User: "+ email);
    	String evaluation = "";
        Optional<User> user = userRepository.findByEmail(email);
            
        StockfishService stockfish = new StockfishService();
        String stockfishPath = "/usr/games/stockfish";
                
        if (stockfish.startEngine(stockfishPath)) {
        	System.out.println("Stockfish engine started successfully");
            evaluation = stockfish.evaluateFEN(fen);
            stockfish.stopEngine();
        } else {
         System.out.println("Failed to start Stockfish engine");
        }
        return evaluation;
    }
    
    public String get_stadistics(String fen, String token) {
    	String email = jwtTokenProvider.getUsername(token.substring(7));
    	System.out.println("User: "+ email);
    	String stadistics = "";
        Optional<User> user = userRepository.findByEmail(email);
            
        StockfishService stockfish = new StockfishService();
        String stockfishPath = "/usr/games/stockfish";
                
        if (stockfish.startEngine(stockfishPath)) {
        	System.out.println("Stockfish engine started successfully");
        	stadistics = stockfish.evaluateFENtoJSON(fen);
            stockfish.stopEngine();
        } else {
         System.out.println("Failed to start Stockfish engine");
        }
        return stadistics;
    }

}

