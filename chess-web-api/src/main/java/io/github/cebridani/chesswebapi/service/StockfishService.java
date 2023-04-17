package io.github.cebridani.chesswebapi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class StockfishService {

    private Process process;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;

    public boolean startEngine(String stockfishPath) {
        ProcessBuilder processBuilder = new ProcessBuilder(stockfishPath);
        try {
            process = processBuilder.start();
            processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            processWriter = new OutputStreamWriter(process.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void stopEngine() {
        try {
            sendCommand("quit");
            processReader.close();
            processWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getOutput(int waitTime) {
        StringBuilder buffer = new StringBuilder();
        try {
            Thread.sleep(waitTime);
            while (processReader.ready()) {
                buffer.append(processReader.readLine()).append("\n");
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
    
    public String getBestMove(String fen, int searchDepth) {
        sendCommand("position fen " + fen);
        sendCommand("go depth " + searchDepth);
        String output = getOutput(5000);

        // Encuentra la mejor jugada en la salida del motor
        String bestMoveLine = output.lines()
                .filter(line -> line.startsWith("bestmove"))
                .findFirst()
                .orElse(null);

        if (bestMoveLine != null) {
            return bestMoveLine.split(" ")[1];
        } else {
            return null;
        }
    }
}

