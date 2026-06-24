package com.lab.robot.common.protocol;

public final class Position {
    private final int x;
    private final int y;
    private final int z;

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d)", x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return x == p.x && y == p.y && z == p.z;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * x + y) + z;
    }
}