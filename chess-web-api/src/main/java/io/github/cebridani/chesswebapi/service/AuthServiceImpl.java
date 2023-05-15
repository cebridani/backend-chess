package io.github.cebridani.chesswebapi.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.cebridani.chesswebapi.entity.Role;
import io.github.cebridani.chesswebapi.entity.User;
import io.github.cebridani.chesswebapi.exception.ChessAPIException;
import io.github.cebridani.chesswebapi.payload.LoginDto;
import io.github.cebridani.chesswebapi.payload.RegisterDto;
import io.github.cebridani.chesswebapi.repository.RoleRepository;
import io.github.cebridani.chesswebapi.repository.UserRepository;
import io.github.cebridani.chesswebapi.security.JwtTokenProvider;

@Service
public class AuthServiceImpl implements AuthService {

	private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;


    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String login(LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        return token;
    }

    @Override
    public String register(RegisterDto registerDto) {
    	try {
    		// add check for username exists in database
            if(userRepository.existsByUsername(registerDto.getUsername())){
                throw new ChessAPIException(HttpStatus.BAD_REQUEST, "User already exists!.");
            }

            // add check for email exists in database
            if(userRepository.existsByEmail(registerDto.getEmail())){
                throw new ChessAPIException(HttpStatus.BAD_REQUEST, "Email is already exists!.");
            }
    	}catch(Exception e) {
    		return HttpStatus.BAD_REQUEST.toString();
    	}
        

        User user = new User();
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        return "User registered successfully!.";
    }
}
