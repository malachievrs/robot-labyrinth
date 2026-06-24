package com.lab.robot.game.server;

import com.lab.robot.common.protocol.Position;
import com.lab.robot.game.engine.Labyrinth;
import com.lab.robot.game.engine.LabyrinthData;
import com.lab.robot.game.engine.GameEngine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    private final int port;
    private final GameEngine engine;

    public GameServer(int port, Labyrinth labyrinth, Position startPosition) {
        this.port = port;
        this.engine = new GameEngine(labyrinth, startPosition);
    }

    public void start() {
        System.out.println("Starting game server on port " + port + "...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening. Press Ctrl+C to stop.");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client: " + clientSocket.getInetAddress());
                ClientHandler handler = new ClientHandler(clientSocket, engine);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        String labyrinthFile = resolveLabyrinthPath(args);

        for (String arg : args) {
            if (arg.matches("\\d+")) {
                port = Integer.parseInt(arg);
            }
        }

        try {
            System.out.println("=== Game Server ===");
            System.out.println("Labyrinth file: " + labyrinthFile);
            LabyrinthData data = LabyrinthData.loadFromFile(labyrinthFile);
            Labyrinth labyrinth = data.getLabyrinth();
            Position start = data.getStartPosition();
            System.out.println("Labyrinth loaded: " + labyrinth.getSizeX() + "x"
                    + labyrinth.getSizeY() + "x" + labyrinth.getSizeZ());
            System.out.println("Start position: " + start);

            GameServer server = new GameServer(port, labyrinth, start);
            server.start();
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String resolveLabyrinthPath(String[] args) {
        if (args.length >= 1 && !args[0].startsWith("--") && !args[0].matches("\\d+")) {
            return args[0];
        }
        String[] candidates = {
                "src/main/resources/labyrinth.txt",
                "../src/main/resources/labyrinth.txt",
                "../../src/main/resources/labyrinth.txt"
        };
        for (String path : candidates) {
            if (java.nio.file.Files.isRegularFile(java.nio.file.Path.of(path))) {
                return path;
            }
        }
        return candidates[0];
    }
}
