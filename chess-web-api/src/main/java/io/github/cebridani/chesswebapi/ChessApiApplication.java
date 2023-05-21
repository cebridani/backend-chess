package io.github.cebridani.chesswebapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChessApiApplication {

	public static void main(String[] args) {
		
        System.out.println("Starting backend-chess...");
		SpringApplication.run(ChessApiApplication.class, args);
	}
}
