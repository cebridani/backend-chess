package io.github.cebridani.chesswebapi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class StockfishService {

    private Process stockfishProcess;
    private PrintWriter out;
    private BufferedReader in;

    public void start() throws IOException {
        // Cambia la ruta al ejecutable de Stockfish en tu sistema
        String stockfishPath = "C:/Chess/Stockfish/stockfish_15_win_x64_avx2";
        ProcessBuilder processBuilder = new ProcessBuilder(stockfishPath);
        stockfishProcess = processBuilder.start();
        out = new PrintWriter(stockfishProcess.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        stockfishProcess.destroy();
    }

    public String sendCommand(String command) throws IOException {
        out.println(command);
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
            if (line.contains("bestmove")) {
                break;
            }
        }

        return response.toString();
    }
}

