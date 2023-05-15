package io.github.cebridani.chesswebapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.CrossOrigin;

import io.github.cebridani.chesswebapi.payload.JWTAuthResponse;
import io.github.cebridani.chesswebapi.payload.LoginDto;
import io.github.cebridani.chesswebapi.payload.RegisterDto;
import io.github.cebridani.chesswebapi.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Build Login REST API
    @CrossOrigin(origins = "http://192.168.49.2:31621")
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginDto loginDto){
    	String token = authService.login(loginDto);

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(token);
        
        System.out.println("User login");

        return ResponseEntity.ok(jwtAuthResponse);
    }

    // Build Register REST API
    @CrossOrigin(origins = "http://192.168.49.2:31621")
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        String response = authService.register(registerDto);
        if(response == HttpStatus.BAD_REQUEST.toString()) {
        	return (ResponseEntity<String>) ResponseEntity.badRequest();
        }
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(value = {"/hola_mundo"})
    public String helloWorld() {
        return "Hola Mundo";
    }
}
