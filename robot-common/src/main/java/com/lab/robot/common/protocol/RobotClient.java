package com.lab.robot.common.protocol;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RobotClient implements AutoCloseable {
    private final String host;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public RobotClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        if (socket != null && !socket.isClosed()) disconnect();
        socket = new Socket(host, port);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public void close() { disconnect(); }

    public void disconnect() {
        try { if (in != null) in.close(); } catch (IOException ignored) {}
        try { if (out != null) out.close(); } catch (Exception ignored) {}
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        in = null; out = null; socket = null;
    }

    private RobotResponse sendCommand(JSONObject cmd) throws IOException, RobotException {
        if (out == null || in == null) throw new IOException("Not connected");
        out.println(cmd.toString());
        out.flush();
        String line = in.readLine();
        if (line == null) throw new IOException("Connection closed by server");
        JSONObject resp = new JSONObject(line);
        String status = resp.getString("status");
        if ("ERROR".equals(status)) throw new RobotException(resp.optString("message", "Server error"));
        return new RobotResponse(status, resp);
    }

    public RobotResponse move(String dir) throws IOException, RobotException {
        JSONObject cmd = new JSONObject().put("command", "MOVE").put("direction", dir.toUpperCase());
        return sendCommand(cmd);
    }

    public RobotResponse measure(String dir) throws IOException, RobotException {
        JSONObject cmd = new JSONObject().put("command", "MEASURE").put("direction", dir.toUpperCase());
        return sendCommand(cmd);
    }

    public RobotResponse getPosition() throws IOException, RobotException {
        JSONObject cmd = new JSONObject().put("command", "GET_POSITION");
        return sendCommand(cmd);
    }

    public LabyrinthMap getLabyrinth() throws IOException, RobotException {
        JSONObject cmd = new JSONObject().put("command", "GET_LABYRINTH");
        RobotResponse resp = sendCommand(cmd);
        JSONObject data = resp.getData();
        int sx = data.getInt("sizeX");
        int sy = data.getInt("sizeY");
        int sz = data.getInt("sizeZ");

        JSONArray layers = data.getJSONArray("grid");
        char[][][] grid = new char[sz][sy][sx];
        for (int z = 0; z < sz; z++) {
            JSONArray rows = layers.getJSONArray(z);
            for (int y = 0; y < sy; y++) {
                String row = rows.getString(y);
                for (int x = 0; x < sx; x++) {
                    grid[z][y][x] = row.charAt(x);
                }
            }
        }

        JSONArray exitsArr = data.getJSONArray("exits");
        List<Position> exits = new ArrayList<>();
        for (int i = 0; i < exitsArr.length(); i++) {
            JSONObject ep = exitsArr.getJSONObject(i);
            exits.add(new Position(ep.getInt("x"), ep.getInt("y"), ep.getInt("z")));
        }
        return new LabyrinthMap(sx, sy, sz, grid, exits);
    }
}