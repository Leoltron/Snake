package ru.leoltron.snake.network;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.MPServerGame;
import ru.leoltron.snake.game.controller.AdaptedMultiLevelGameController;
import ru.leoltron.snake.game.controller.snake.SnakeController;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

public class MPServer {
    private static final Pattern CLIENT_UPDATE_PACKET_PATTERN = Pattern.compile("([\\d]+):([\\w]+)");
    private MPServerGame game;
    private int playersAmount;
    private SnakeController[] controllers;
    private ClientInfo[] clientInfos;
    private Socket[] clientSockets;
    private PrintWriter[] printWriters;
    private long lastPacketSendTime = 0;

    public MPServer(int port) {
        this(port, 2);
    }

    public MPServer(int port, int playersAmount) {
        info(String.format("Starting server at port %d...", port));
        this.playersAmount = playersAmount;
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        controllers = new SnakeController[playersAmount];
        clientInfos = new ClientInfo[playersAmount];
        clientSockets = new Socket[playersAmount];
        printWriters = new PrintWriter[playersAmount];
        info("Initializing game...");
        for (int i = 0; i < playersAmount; i++) controllers[i] = new SnakeController(i);
        game = new MPServerGame(new AdaptedMultiLevelGameController(controllers), 64, 32);
        info(String.format("Game initialized, waiting for %d player(s) to connect...", playersAmount));
        for (int i = 0; i < playersAmount; i++) {
            info("Waiting for player #" + i + "...");
            try {
                clientSockets[i] = serverSocket.accept();
                info(clientSockets[i].toString() + " connected.");
            } catch (IOException e) {
                e.printStackTrace();
                i--;
                continue;
            }
            clientInfos[i] = new ClientInfo(-1, -1);
            try {
                printWriters[i] = new PrintWriter(clientSockets[i].getOutputStream(), true);
                new ClientInputHandlerThread(i, clientSockets[i]).start();
            } catch (IOException e) {
                e.printStackTrace();
                i--;
                continue;
            }
        }
        info("All players connected, starting game...");
        game.startNewGame();
        val period = 300;
        val timer = new Timer(period, actionEvent -> tickGameAndUpdate());
        timer.start();
        info("Done.");
    }

    private void info(String message) {
        System.out.println("[INFO] [MPServer] " + message);
    }

    private void tickGameAndUpdate() {
        game.tick(false);
        broadcastUpdatePacket();
    }

    public void broadcastUpdatePacket() {
        info("Getting update packet...");
        val curTick = game.getTime();
        val packet = game.getChangePacket(curTick);
        info("Message: " + curTick + ":(clientInfo):" + packet);
        info("Broadcasting update packet...");
        for (int i = 0; i < playersAmount; i++) {
            val lastTick = clientInfos[i].lastPacketTickReceived <= 0 ? 0 : clientInfos[i].lastPacketTickReceived;
            val delay = clientInfos[i].lastPacketDelay <= 0 ? 0 : clientInfos[i].lastPacketDelay;
            sendTo(i, String.format("%d:%d:%d:" + packet, curTick, lastTick, delay));
            clientInfos[i].lastPacketDelay = -1;
        }
        lastPacketSendTime = System.currentTimeMillis();
    }

    private void sendTo(int id, String message) {
        printWriters[id].println(message);
    }

    @Data
    @AllArgsConstructor
    private static class ClientInfo {

        private long lastPacketDelay;
        private int lastPacketTickReceived;
    }

    private class ClientInputHandlerThread extends Thread {
        private final int id;
        private Socket socket;
        private BufferedReader in;
        private final String logPrefix;

        public ClientInputHandlerThread(int id, Socket socket) throws IOException {
            super("ClientInputHandlerThread");
            this.id = id;
            logPrefix = " [ClientInputHandlerThread #" + this.id + "] ";
            info("Initializing...");
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            info("Initialized.");
        }

        private void info(String message) {
            System.out.println("[INFO]" + logPrefix + message);
        }

        private void warn(String message) {
            System.err.println("[WARNING]" + logPrefix + message);
        }

        @Override
        public void run() {
            info("Running...");
            String inputLine;
            try {
                while (!socket.isClosed() && (inputLine = in.readLine()) != null) {
                    info("Received message " + inputLine);
                    val matcher = CLIENT_UPDATE_PACKET_PATTERN.matcher(inputLine);
                    if (!matcher.matches())
                        warn(String.format("Cannot handle client #%d message: %s", id, inputLine));
                    val tick = Integer.parseInt(matcher.group(1));
                    val newDir = Direction.valueOf(matcher.group(2));
                    info(String.format("Packet successfully validated, tick: %d, new direction: %s", tick, newDir.name()));
                    if (clientInfos[id].lastPacketDelay <= 0)
                        clientInfos[id].lastPacketDelay = System.currentTimeMillis() - lastPacketSendTime;
                    clientInfos[id].lastPacketTickReceived = tick;
                    controllers[id].setCurrentDirection(newDir);
                }
            } catch (IOException e) {
                warn("An error occurred during reading message:");
                e.printStackTrace();
            } finally {
                if (in != null)
                    try {
                        info("Closing socket's BufferedReader...");
                        in.close();
                        info("Closed.");
                    } catch (IOException e) {
                        warn("An error occurred during closing BufferedReader:");
                        e.printStackTrace();
                    }
            }
        }

    }
}