package com.lab.robot.game.engine;

import com.lab.robot.common.protocol.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Labyrinth {
    private final Cell[][][] grid; // [x][y][z]
    private final int sizeX, sizeY, sizeZ;
    private final List<Position> exits = new ArrayList<>();

    public Labyrinth(int sizeX, int sizeY, int sizeZ) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.grid = new Cell[sizeX][sizeY][sizeZ];
        for (int x = 0; x < sizeX; x++)
            for (int y = 0; y < sizeY; y++)
                for (int z = 0; z < sizeZ; z++)
                    grid[x][y][z] = new Cell(false, false);
    }

    public int getSizeX() { return sizeX; }
    public int getSizeY() { return sizeY; }
    public int getSizeZ() { return sizeZ; }

    public Cell getCell(int x, int y, int z) {
        return inBounds(x, y, z) ? grid[x][y][z] : null;
    }

    public void setObstacle(int x, int y, int z, boolean obstacle) {
        if (inBounds(x, y, z)) grid[x][y][z].setObstacle(obstacle);
    }

    public void setExit(int x, int y, int z, boolean exit) {
        if (inBounds(x, y, z)) {
            grid[x][y][z].setExit(exit);
            if (exit) {
                exits.add(new Position(x, y, z));
            } else {
                exits.removeIf(p -> p.getX() == x && p.getY() == y && p.getZ() == z);
            }
        }
    }

    public List<Position> getExits() {
        return Collections.unmodifiableList(exits);
    }

    private boolean inBounds(int x, int y, int z) {
        return x >= 0 && x < sizeX && y >= 0 && y < sizeY && z >= 0 && z < sizeZ;
    }

    public int getDistance(Position pos, Direction dir) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        int dx = dir.getDx(), dy = dir.getDy(), dz = dir.getDz();
        int count = 0;
        while (true) {
            x += dx; y += dy; z += dz;
            Cell c = getCell(x, y, z);
            if (c == null || c.isObstacle()) return count;
            count++;
        }
    }
}