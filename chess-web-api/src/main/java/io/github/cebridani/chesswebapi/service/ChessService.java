package io.github.cebridani.chesswebapi.service;

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
import io.github.cebridani.chesswebapi.payload.FenDto;
import io.github.cebridani.chesswebapi.repository.UserRepository;
import io.github.cebridani.chesswebapi.security.JwtTokenProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class ChessService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ChessService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String processFen(FenDto fenDto, String token) throws JsonMappingException, JsonProcessingException {        
        
        String email = jwtTokenProvider.getUsername(token.substring(7));
        Optional<User> user = userRepository.findByEmail(email);

        StockfishService stockfish = new StockfishService();
        String stockfishPath = "/usr/games/stockfish";
        
        String bestMove = "";
	    
	if (stockfish.startEngine(stockfishPath)) {
		
	    System.out.println("Stockfish engine started successfully");
		bestMove = stockfish.getBestMove(fenDto.getFen(), 15);
		System.out.println(bestMove);
		stockfish.stopEngine();

	} else {
            System.out.println("Failed to start Stockfish engine");
    }
	    
		return bestMove;
    }

    public FenDto getFenByUser(String token) {
	String email = jwtTokenProvider.getUsername(token.substring(7));
	System.out.println("User: "+ email);
        Optional<User> user = userRepository.findByEmail(email);

        return new FenDto(user.get().getFen()); 
    }

    public void setFenByUser(FenDto fenDto, String token) {
	String email = jwtTokenProvider.getUsername(token.substring(7));
	System.out.println("User: "+ email);
        Optional<User> user = userRepository.findByEmail(email);
        
        String newFen = fenDto.getFen();
        
        user.get().setFen(newFen);
        userRepository.save(user.get());        
        
    }
	
    public String list_best_moves(FenDto fenDto, String token) throws JsonMappingException, JsonProcessingException {        
        
        String email = jwtTokenProvider.getUsername(token.substring(7));
        Optional<User> user = userRepository.findByEmail(email);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FenDto> entity = new HttpEntity<>(fenDto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://127.0.0.1:5000/top_moves", entity, String.class);
        String responseBody = response.getBody();


        System.out.println(responseBody);
        
        return responseBody;
    }
}

