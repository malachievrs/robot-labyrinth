package com.lab.robot.common.protocol;

import java.util.List;

public class LabyrinthMap {
    private final int sizeX, sizeY, sizeZ;
    private final char[][][] grid;
    private final List<Position> exits;

    public LabyrinthMap(int sizeX, int sizeY, int sizeZ, char[][][] grid, List<Position> exits) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.grid = grid;
        this.exits = exits;
    }

    public int getSizeX() { return sizeX; }
    public int getSizeY() { return sizeY; }
    public int getSizeZ() { return sizeZ; }
    public char[][][] getGrid() { return grid; }
    public List<Position> getExits() { return exits; }

    public char[][] getLayer(int z) {
        if (z < 0 || z >= sizeZ) return new char[0][0];
        char[][] layer = new char[sizeY][sizeX];
        for (int y = 0; y < sizeY; y++) {
            System.arraycopy(grid[z][y], 0, layer[y], 0, sizeX);
        }
        return layer;
    }
}