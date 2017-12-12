package ru.leoltron.snake.network;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.MPServerGame;
import ru.leoltron.snake.game.controller.MultiLevelGameController;
import ru.leoltron.snake.game.controller.snake.SimpleAISnakeController;
import ru.leoltron.snake.game.controller.snake.SnakeController;
import ru.leoltron.snake.util.LogUtils;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

import static ru.leoltron.snake.util.LogUtils.findFreeFile;
import static ru.leoltron.snake.util.LogUtils.getTodayDateString;

public class MPServer {
    private static final Pattern CLIENT_UPDATE_PACKET_PATTERN = Pattern.compile("([\\d]+):([\\w]+)");
    private MPServerGame game;
    private int playersAmount;
    private SnakeController[] controllers;
    private ClientInfo[] clientInfos;
    private Socket[] clientSockets;
    private PrintWriter[] printWriters;
    private long lastPacketSendTime = 0;

    private static String logFilename = findFreeFile("logs/log_server_" + getTodayDateString(), ".log");

    private static void log(String message, PrintStream primaryStream) {
        LogUtils.log(logFilename, message, primaryStream);
    }

    private void info(String message) {
        log("[INFO] [MPServer] " + message, System.out);
    }

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
        controllers = new SnakeController[playersAmount + 1];
        clientInfos = new ClientInfo[playersAmount];
        clientSockets = new Socket[playersAmount];
        printWriters = new PrintWriter[playersAmount];
        info("Initializing game...");
        for (int i = 0; i < playersAmount; i++) controllers[i] = new SnakeController(i);
        controllers[playersAmount] = new SimpleAISnakeController(playersAmount);
        game = new MPServerGame(new MultiLevelGameController(controllers), 64, 32);
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
            clientInfos[i] = new ClientInfo(-1, -1, 0);
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

    private void tickGameAndUpdate() {
        game.tick(false);
        broadcastUpdatePacket();
    }

    public void broadcastUpdatePacket() {
        info("Getting update packet...");
        val curTick = game.getTime();
        val packet = game.getChangePacket(curTick);
        info("Message: " + curTick + ":(clientInfo):" + packet.replace("\n", "\n\t"));
        info("Broadcasting update packet...");
        for (int i = 0; i < playersAmount; i++) {
            val packetTickDelay = clientInfos[i].packetTickDelay;
            val delay = clientInfos[i].lastPacketDelay <= 0 ? 0 : clientInfos[i].lastPacketDelay;
            sendTo(i, String.format("%d:%d:%d:" + packet, curTick, packetTickDelay, delay));
            clientInfos[i].lastPacketDelay = -1;
        }
        lastPacketSendTime = System.currentTimeMillis();
    }

    private void sendTo(int id, String message) {
        printWriters[id].println(message);
    }

    private void updateClientDirection(int clientId, Direction newDirection, int packetTick) {
        controllers[clientId].setCurrentDirection(newDirection);
        clientInfos[clientId].setPacketTickDelay(game.getTime() - packetTick);
    }

    @Data
    @AllArgsConstructor
    private static class ClientInfo {

        private long lastPacketDelay;
        private int lastPacketTickReceived;
        private int packetTickDelay;
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
            log("[INFO]" + logPrefix + message, System.out);
        }

        private void error(String message) {
            log("[ERROR]" + logPrefix + message, System.err);
        }

        @Override
        public void run() {
            info("Running...");
            String inputLine;
            try {
                while (!socket.isClosed() && (inputLine = in.readLine()) != null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    info("Received message " + inputLine);
                    val matcher = CLIENT_UPDATE_PACKET_PATTERN.matcher(inputLine);
                    if (!matcher.matches())
                        error(String.format("Cannot handle client #%d message: %s", id, inputLine));
                    val tick = Integer.parseInt(matcher.group(1));
                    val newDir = Direction.valueOf(matcher.group(2));
                    info(String.format("Packet successfully validated, tick: %d, new direction: %s", tick, newDir.name()));
                    if (clientInfos[id].lastPacketTickReceived != tick)
                        clientInfos[id].lastPacketDelay = System.currentTimeMillis() - lastPacketSendTime;
                    clientInfos[id].lastPacketTickReceived = tick;

                    updateClientDirection(id, newDir, tick);
                }
            } catch (IOException e) {
                error("An error occurred during reading message:");
                e.printStackTrace();
            } finally {
                if (in != null)
                    try {
                        info("Closing socket's BufferedReader...");
                        in.close();
                        info("Closed.");
                    } catch (IOException e) {
                        error("An error occurred during closing BufferedReader:");
                        e.printStackTrace();
                    }
            }
        }

    }
}
