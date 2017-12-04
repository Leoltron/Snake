package ru.leoltron.snake.network;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.MPServerGame;
import ru.leoltron.snake.game.controller.MultiLevelGameController;
import ru.leoltron.snake.game.controller.snake.SnakeController;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

@Log
public class ServerProtocol {
    private static final Pattern CLIENT_UPDATE_PACKET_PATTERN = Pattern.compile("([\\d]+):([\\w]+)");
    private MPServerGame game;
    private int playersAmount;
    private SnakeController[] controllers;
    private ClientInfo[] clientInfos;
    private Socket[] clientSockets;
    private PrintWriter[] printWriters;
    private long lastPacketSendTime = 0;

    public ServerProtocol(int port) {
        this(port, 2);
    }

    public ServerProtocol(int port, int playersAmount) {
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
        for (int i = 0; i < playersAmount; i++) controllers[i] = new SnakeController(i);
        game = new MPServerGame(new MultiLevelGameController(controllers), 64, 32);
        for (int i = 0; i < playersAmount; i++) {
            try {
                clientSockets[i] = serverSocket.accept();
                log.info(clientSockets[i].toString() + " connected.");
            } catch (IOException e) {
                e.printStackTrace();
                i--;
                continue;
            }
            clientInfos[i] = new ClientInfo(-1, -1);
            try {
                printWriters[i] = new PrintWriter(clientSockets[i].getOutputStream(), true);
                new ClientInputHandlerThread(i, clientSockets[i]).run();
            } catch (IOException e) {
                e.printStackTrace();
                i--;
                continue;
            }
        }
        game.startNewGame();
        val period = 300;
        val timer = new Timer(period, actionEvent -> tickGameAndUpdate());
        timer.start();
    }

    private void tickGameAndUpdate() {
        game.tick();
        broadcastUpdatePacket();
    }

    public void broadcastUpdatePacket() {
        val curTick = game.getTime();
        val packet = game.getChangePacket(curTick);
        for (int i = 0; i < playersAmount; i++) {
            val lastTick = clientInfos[i].lastPacketTickReceived <= 0 ? 0 : clientInfos[i].lastPacketTickReceived;
            val delay = clientInfos[i].lastPacketDelay <= 0 ? 0 : clientInfos[i].lastPacketDelay;
            sendTo(i, String.format("%d:%d:%d" + packet, curTick, lastTick, delay));
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

        public ClientInputHandlerThread(int id, Socket socket) throws IOException {
            super("ClientInputHandlerThread");
            this.id = id;
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            String inputLine;
            try {
                while (!socket.isClosed() && (inputLine = in.readLine()) != null) {
                    val matcher = CLIENT_UPDATE_PACKET_PATTERN.matcher(inputLine);
                    if (!matcher.matches())
                        log.warning(String.format("Cannot handle client #%d message: %s", id, inputLine));
                    val tick = Integer.parseInt(matcher.group(1));
                    val newDir = Direction.valueOf(matcher.group(2));
                    if (clientInfos[id].lastPacketDelay <= 0)
                        clientInfos[id].lastPacketDelay = System.currentTimeMillis() - lastPacketSendTime;
                    clientInfos[id].lastPacketTickReceived = tick;
                    controllers[id].setCurrentDirection(newDir);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }

    }
}
