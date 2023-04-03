package io.github.cebridani.chesswebapi.service.impl;

import org.springframework.http.ResponseEntity;

import io.github.cebridani.chesswebapi.payload.LoginDto;
import io.github.cebridani.chesswebapi.payload.RegisterDto;

public interface AuthService {
	public ResponseEntity<String> login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}