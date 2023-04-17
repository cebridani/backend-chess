package io.github.cebridani.chesswebapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cebridani.chesswebapi.service.StockfishService;

@SpringBootApplication
public class ChessApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChessApiApplication.class, args);
		
		StockfishService stockfish = new StockfishService();
		try {
			stockfish.start();
			System.out.println(stockfish.sendCommand("ucinewgame"));
			System.out.println(stockfish.sendCommand("go movetime 1000"));
			stockfish.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
