package com.lab.robot.game.server;

import com.lab.robot.common.protocol.Position;
import com.lab.robot.game.engine.Direction;
import com.lab.robot.game.engine.GameEngine;
import com.lab.robot.game.engine.Labyrinth;
import com.lab.robot.game.engine.Cell;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final GameEngine engine;

    public ClientHandler(Socket socket, GameEngine engine) {
        this.socket = socket;
        this.engine = engine;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line;
            while ((line = in.readLine()) != null) {
                JSONObject request;
                try {
                    request = new JSONObject(line);
                } catch (Exception e) {
                    sendError(out, "Invalid JSON");
                    continue;
                }

                String cmd = request.optString("command", "").toUpperCase();
                JSONObject response;

                switch (cmd) {
                    case "MOVE":          response = handleMove(request); break;
                    case "MEASURE":       response = handleMeasure(request); break;
                    case "GET_POSITION":  response = handleGetPosition(); break;
                    case "GET_LABYRINTH": response = handleGetLabyrinth(); break;
                    default:
                        response = createErrorResponse("Unknown command: " + cmd);
                }
                out.println(response.toString());
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private JSONObject handleMove(JSONObject req) {
        String dirStr = req.optString("direction", "");
        Direction dir;
        try {
            dir = Direction.fromString(dirStr);
        } catch (IllegalArgumentException e) {
            return createErrorResponse("Unknown direction: " + dirStr);
        }
        GameEngine.MoveResult res = engine.move(dir);
        JSONObject resp = new JSONObject();
        resp.put("status", res.getStatus().name());
        resp.put("message", res.getMessage());
        if (res.getStatus() != GameEngine.MoveResult.Status.ERROR)
            resp.put("position", positionToJson(res.getPosition()));
        return resp;
    }

    private JSONObject handleMeasure(JSONObject req) {
        String dirStr = req.optString("direction", "");
        Direction dir;
        try {
            dir = Direction.fromString(dirStr);
        } catch (IllegalArgumentException e) {
            return createErrorResponse("Unknown direction: " + dirStr);
        }
        GameEngine.MeasureResult res = engine.measure(dir);
        JSONObject resp = new JSONObject();
        resp.put("status", res.getStatus().name());
        resp.put("message", res.getMessage());
        if (res.getStatus() == GameEngine.MeasureResult.Status.OK)
            resp.put("distance", res.getDistance());
        return resp;
    }

    private JSONObject handleGetPosition() {
        GameEngine.PositionResult res = engine.getPositionInfo();
        JSONObject resp = new JSONObject();
        resp.put("status", res.getStatus().name());
        resp.put("message", res.getMessage());
        resp.put("position", positionToJson(res.getPosition()));
        JSONObject cell = new JSONObject();
        cell.put("obstacle", res.isCellObstacle());
        cell.put("exit", res.isExit());
        resp.put("cell", cell);
        return resp;
    }

    private JSONObject handleGetLabyrinth() {
        Labyrinth lab = engine.getLabyrinth();
        JSONObject resp = new JSONObject();
        resp.put("status", "OK");
        resp.put("sizeX", lab.getSizeX());
        resp.put("sizeY", lab.getSizeY());
        resp.put("sizeZ", lab.getSizeZ());

        JSONArray layers = new JSONArray();
        for (int z = 0; z < lab.getSizeZ(); z++) {
            JSONArray rows = new JSONArray();
            for (int y = 0; y < lab.getSizeY(); y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = 0; x < lab.getSizeX(); x++) {
                    Cell cell = lab.getCell(x, y, z);
                    if (cell.isObstacle()) sb.append('#');
                    else if (cell.isExit()) sb.append('E');
                    else sb.append('.');
                }
                rows.put(sb.toString());
            }
            layers.put(rows);
        }
        resp.put("grid", layers);

        JSONArray exitsArr = new JSONArray();
        for (Position p : lab.getExits()) {
            JSONObject exit = new JSONObject();
            exit.put("x", p.getX());
            exit.put("y", p.getY());
            exit.put("z", p.getZ());
            exitsArr.put(exit);
        }
        resp.put("exits", exitsArr);
        return resp;
    }

    private JSONObject createErrorResponse(String msg) {
        JSONObject r = new JSONObject();
        r.put("status", "ERROR");
        r.put("message", msg);
        return r;
    }

    private void sendError(PrintWriter out, String msg) {
        out.println(createErrorResponse(msg).toString());
        out.flush();
    }

    private JSONObject positionToJson(Position p) {
        JSONObject j = new JSONObject();
        if (p != null) {
            j.put("x", p.getX());
            j.put("y", p.getY());
            j.put("z", p.getZ());
        }
        return j;
    }
}
