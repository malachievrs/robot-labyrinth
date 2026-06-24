package com.lab.robot.game.engine;

import com.lab.robot.common.protocol.Position;
import java.util.Objects;

public class GameEngine {
    private Position position;
    private boolean broken;
    private final Labyrinth labyrinth;

    public GameEngine(Labyrinth labyrinth, Position startPosition) {
        this.labyrinth = Objects.requireNonNull(labyrinth);
        this.position = Objects.requireNonNull(startPosition);
        this.broken = false;

        Cell startCell = labyrinth.getCell(startPosition.getX(), startPosition.getY(), startPosition.getZ());
        if (startCell == null || startCell.isObstacle()) {
            throw new IllegalArgumentException("Start position is outside or obstacle");
        }
    }

    public Position getPosition() { return position; }
    public boolean isBroken() { return broken; }
    public Labyrinth getLabyrinth() { return labyrinth; }

    public MoveResult move(Direction dir) {
        if (broken) return new MoveResult(MoveResult.Status.BROKEN, position, "Robot is broken");

        int nx = position.getX() + dir.getDx();
        int ny = position.getY() + dir.getDy();
        int nz = position.getZ() + dir.getDz();
        Cell target = labyrinth.getCell(nx, ny, nz);
        if (target == null) {
            broken = true;
            return new MoveResult(MoveResult.Status.OBSTACLE, position, "Outside labyrinth");
        }
        if (target.isObstacle()) {
            broken = true;
            return new MoveResult(MoveResult.Status.OBSTACLE, position, "Crashed into obstacle");
        }
        position = new Position(nx, ny, nz);
        return new MoveResult(MoveResult.Status.OK, position, "");
    }

    public MeasureResult measure(Direction dir) {
        if (broken) return new MeasureResult(MeasureResult.Status.BROKEN, -1, "Robot is broken");
        int dist = labyrinth.getDistance(position, dir);
        return new MeasureResult(MeasureResult.Status.OK, dist, "");
    }

    public PositionResult getPositionInfo() {
        if (broken)
            return new PositionResult(PositionResult.Status.BROKEN, position, false, false, "Robot is broken");

        Cell cell = labyrinth.getCell(position.getX(), position.getY(), position.getZ());
        boolean isObstacle = cell != null && cell.isObstacle();
        boolean isExit = cell != null && cell.isExit();
        return new PositionResult(PositionResult.Status.OK, position, isObstacle, isExit, "");
    }

    public static class MoveResult {
        public enum Status { OK, OBSTACLE, BROKEN, ERROR }
        private final Status status;
        private final Position position;
        private final String message;

        public MoveResult(Status s, Position p, String m) { status=s; position=p; message=m; }
        public Status getStatus() { return status; }
        public Position getPosition() { return position; }
        public String getMessage() { return message; }
    }

    public static class MeasureResult {
        public enum Status { OK, BROKEN, ERROR }
        private final Status status;
        private final int distance;
        private final String message;

        public MeasureResult(Status s, int d, String m) { status=s; distance=d; message=m; }
        public Status getStatus() { return status; }
        public int getDistance() { return distance; }
        public String getMessage() { return message; }
    }

    public static class PositionResult {
        public enum Status { OK, BROKEN, ERROR }
        private final Status status;
        private final Position position;
        private final boolean cellObstacle;
        private final boolean exit;
        private final String message;

        public PositionResult(Status s, Position p, boolean obs, boolean exit, String m) {
            this.status = s; this.position = p; this.cellObstacle = obs;
            this.exit = exit; this.message = m;
        }
        public Status getStatus() { return status; }
        public Position getPosition() { return position; }
        public boolean isCellObstacle() { return cellObstacle; }
        public boolean isExit() { return exit; }
        public String getMessage() { return message; }
    }
}