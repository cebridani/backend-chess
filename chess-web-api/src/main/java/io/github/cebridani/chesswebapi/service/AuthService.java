package io.github.cebridani.chesswebapi.service;

import io.github.cebridani.chesswebapi.payload.LoginDto;
import io.github.cebridani.chesswebapi.payload.RegisterDto;

public interface AuthService {
	public String login(LoginDto loginDto);

     public ResponseEntity<String> register(RegisterDto registerDto);
}
