package io.github.cebridani.chesswebapi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import org.json.*;

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
    
    public String getBestMove(String fen) {
        sendCommand("setoption name UCI_Chess960 value false");
        sendCommand("position fen " + fen);
        sendCommand("go movetime 1000");
        String output = getOutput(1100);

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
    
    public List<String> getBestMoves(String fen) {
        sendCommand("setoption name UCI_Chess960 value false");
        sendCommand("setoption name MultiPV value 3"); // Agrega esta línea para obtener los tres mejores movimientos
        sendCommand("position fen " + fen);
        sendCommand("go movetime 1000");
        String output = getOutput(1100);

        // Encuentra las tres mejores jugadas en la salida del motor
        List<String> topThreeMoves = new ArrayList<>();
        output.lines()
            .filter(line -> line.contains(" pv ")) // Filtra las líneas que contienen la variación principal (pv)
            .limit(3) // Limita a 3 resultados
            .forEach(line -> {
                String[] tokens = line.split(" ");
                for (int i = 0; i < tokens.length; i++) {
                    if ("pv".equals(tokens[i]) && i + 1 < tokens.length) {
                        topThreeMoves.add(tokens[i + 1]); // Agrega el siguiente token después de "pv" como un movimiento principal
                        break;
                    }
                }
            });

        return topThreeMoves;
    }

    public String evaluateFEN(String fen) {
        String evaluation = "";

        // Configurar el motor para el modo UCI.
        sendCommand("uci");

        // Configurar el tablero para la posición FEN dada.
        sendCommand("ucinewgame");
        sendCommand("position fen " + fen);

        // Pedir al motor que evalúe la posición.
        sendCommand("go movetime 1000");

        // Esperar a que el motor calcule y luego obtener la salida.
        String output = getOutput(1500);

        // La última línea de la salida debería contener la evaluación.
        String[] lines = output.split("\n");
        // Invertimos el orden de las líneas
        Collections.reverse(Arrays.asList(lines));
        for (String line : lines) {
        	if (line.contains("score cp")) {
        	    int scoreIndex = line.indexOf("score cp") + 9;
        	    int scoreEndIndex = line.indexOf(" ", scoreIndex);
        	    evaluation = line.substring(scoreIndex, scoreEndIndex);
        	    // Divide la evaluación por 100.
        	    evaluation = String.valueOf(Integer.parseInt(evaluation) / 100.0);
        	    // Ajusta la evaluación en función del turno.
        	    if (fen.contains(" b ")) {  // Si es el turno de las negras, invertimos la evaluación.
        	        evaluation = String.valueOf(-Double.parseDouble(evaluation));
        	    }
        	    break;
        	}

            if (line.contains("score mate")) {
                int scoreIndex = line.indexOf("score mate") + 11;
                int scoreEndIndex = line.indexOf(" ", scoreIndex);
                evaluation = "Mate in " + line.substring(scoreIndex, scoreEndIndex) + " moves";
                // Si es el turno de las negras y hay un mate, añadimos un menos a la evaluación
                if (fen.contains(" b ")) {
                    evaluation = "Mate in -" + line.substring(scoreIndex, scoreEndIndex) + " moves";
                }
                break;
            }
        }

        return evaluation;
    }


    public String evaluateFENtoJSON(String fen) {
        // Configurar el motor para el modo UCI.
        sendCommand("uci");

        // Configurar el tablero para la posición FEN dada.
        sendCommand("ucinewgame");
        sendCommand("position fen " + fen);

        // Pedir al motor que evalúe la posición.
        sendCommand("eval");

        // Esperar a que el motor calcule y luego obtener la salida.
        String input = getOutput(1000);

        Map<String, Map<String, String>> boardMap = new HashMap<>();
        String[] positions = {"a", "b", "c", "d", "e", "f", "g", "h"};

        String[] lines = input.split("\n");
        int start = -1;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains("NNUE derived piece values:")) {
                start = i + 2; // Incrementamos en 3 para saltar dos líneas después de "NNUE derived piece values:"
                break;
            }
        }

        if (start == -1) {
            return "No se encontró la tabla del tablero en la entrada proporcionada.";
        }

        int rank = 8;
        for (int i = start; i < start + 23; i += 3) {
            String[] pieces = lines[i].split("\\|");
            String[] values = lines[i + 1].split("\\|");

            for (int j = 1; j < pieces.length; j++) {
                String piece = pieces[j].trim();

                // Asignamos 0 al valor para las piezas 'k' y 'K'
                String value = (piece.equals("k") || piece.equals("K")) ? "0" : (j < values.length ? values[j].trim() : "");

                if (!piece.isEmpty()) {
                    String pos = positions[j - 1] + rank;
                    Map<String, String> pieceMap = new HashMap<>();
                    pieceMap.put("Piece", piece);
                    pieceMap.put("Value", value);
                    boardMap.put(pos, pieceMap);
                }
            }
            rank--;
        }

        // Convertir el mapa a JSON
        Gson gson = new Gson();
        String json = gson.toJson(boardMap);
        
        System.out.println(json);

        return json;
    }

}

