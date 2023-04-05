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

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FenDto> entity = new HttpEntity<>(fenDto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://127.0.0.1:5000/best_move", entity, String.class);
        String responseBody = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});

        String newFen = (String) responseMap.get("fen");
        
        user.get().setFen(newFen);
        userRepository.save(user.get());

        return responseBody;
    }

	public FenDto getFenByUser(String token) {
		String email = jwtTokenProvider.getUsername(token.substring(7));
        Optional<User> user = userRepository.findByEmail(email);

        return new FenDto(user.get().getFen()); 
	}
}

