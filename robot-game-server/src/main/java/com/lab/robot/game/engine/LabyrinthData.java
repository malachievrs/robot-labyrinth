package com.lab.robot.game.engine;

import com.lab.robot.common.protocol.Position;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LabyrinthData {
    private final Labyrinth labyrinth;
    private final Position startPosition;

    public LabyrinthData(Labyrinth labyrinth, Position startPosition) {
        this.labyrinth = labyrinth;
        this.startPosition = startPosition;
    }

    public Labyrinth getLabyrinth() {
        return labyrinth;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public static LabyrinthData loadFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String line = reader.readLine();
            while (line != null && line.trim().isEmpty()) {
                line = reader.readLine();
            }
            if (line == null) throw new IOException("File is empty");

            String[] header = line.trim().split("\\s+");
            if (header.length < 6)
                throw new IOException("Invalid header. Expected: sizeX sizeY sizeZ startX startY startZ");

            int sizeX = Integer.parseInt(header[0]);
            int sizeY = Integer.parseInt(header[1]);
            int sizeZ = Integer.parseInt(header[2]);
            int startX = Integer.parseInt(header[3]);
            int startY = Integer.parseInt(header[4]);
            int startZ = Integer.parseInt(header[5]);

            Position start = new Position(startX, startY, startZ);
            Labyrinth labyrinth = new Labyrinth(sizeX, sizeY, sizeZ);

            for (int z = 0; z < sizeZ; z++) {
                do {
                    line = reader.readLine();
                    if (line == null) throw new IOException("Unexpected end at layer " + z);
                } while (line.trim().isEmpty());

                for (int y = 0; y < sizeY; y++) {
                    if (line == null) throw new IOException("Unexpected end at layer " + z + " row " + y);
                    String row = line.trim();
                    if (row.length() != sizeX) throw new IOException("Row length mismatch at layer " + z + " row " + y);

                    for (int x = 0; x < sizeX; x++) {
                        char c = row.charAt(x);
                        if (c == '#') {
                            labyrinth.setObstacle(x, y, z, true);
                        } else {
                            labyrinth.setObstacle(x, y, z, false);
                            if (c == 'E') {
                                labyrinth.setExit(x, y, z, true);
                            }
                        }
                    }
                    if (y < sizeY - 1) line = reader.readLine();
                }
            }
            return new LabyrinthData(labyrinth, start);
        }
    }
}
