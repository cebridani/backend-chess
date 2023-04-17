package io.github.cebridani.chesswebapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cebridani.chesswebapi.service.StockfishService;

@SpringBootApplication
public class ChessApiApplication {

	public static void main(String[] args) {
		
		StockfishService stockfish = new StockfishService();
       		String stockfishPath = "/usr/games/stockfish";
		
		if (stockfish.startEngine(stockfishPath)) {
		    System.out.println("Stockfish engine started successfully");

		    stockfish.sendCommand("uci");
		    System.out.println(stockfish.getOutput(1000));

		    stockfish.sendCommand("setoption name Threads value 2");
		    stockfish.sendCommand("isready");
		    System.out.println(stockfish.getOutput(1000));

		    stockfish.sendCommand("ucinewgame");
		    stockfish.sendCommand("position startpos");
		    stockfish.sendCommand("go depth 15");
		    System.out.println(stockfish.getOutput(5000));

		    String initialFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		    int searchDepth = 15;
		    String bestMove = stockfish.getBestMove(initialFEN, searchDepth);
		    System.out.println(bestMove);

		    stockfish.stopEngine();
		} else {
		    System.out.println("Failed to start Stockfish engine");
		}
		
		SpringApplication.run(ChessApiApplication.class, args);
	}
}
