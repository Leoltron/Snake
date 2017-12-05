package ru.leoltron.snake.network;

import lombok.val;
import ru.leoltron.snake.game.CurrentDirectionHolder;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.MPClientGame;
import ru.leoltron.snake.gui.GameFrame;
import ru.leoltron.snake.gui.GameKeyListener;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class MPClient implements WindowListener, CurrentDirectionHolder {

    private static final Pattern UPDATE_PACKET_PATTERN = Pattern.compile("([\\d]+):([\\d]+):([\\d]+):([\\d]+)");

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MPClientGame game;

    public MPClient(JFrame parentFrame, JPanel parentPanel, String hostname, int port) {
        try {
            socket = new Socket(hostname, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(parentPanel, "Неизвестный хост " + hostname,
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(System.err);
            closeAll();
            return;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentPanel,
                    "Couldn't get I/O for the connection to " + hostname,
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(System.err);
            closeAll();
            return;
        }

        game = new MPClientGame(64, 32);
        game.setCurrentDirection(Direction.DOWN);
        GameFrame frame;
        try {
            frame = new GameFrame(game, 0.25d);
        } catch (IOException e) {
            e.printStackTrace();
            closeAll();
            return;
        }
        frame.setParentFrame(parentFrame);
        parentFrame.setVisible(false);
        frame.addWindowListener(this);
        frame.addKeyListener(new GameKeyListener(this, GameKeyListener.WASD_KEYS));

        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Received packet from server, processing...");
                    System.out.println("Message: " + line);
                    val match = UPDATE_PACKET_PATTERN.matcher(line);
                    if (!match.matches()) {
                        System.err.println("Invalid update packet: " + line);
                        continue;
                    }
                    System.out.println("Packet validated, sending reply...");
                    sendDirectionUpdatePacket();

                    int currentTick = Integer.parseInt(match.group(1));
                    int lastPacketReceivedTick = Integer.parseInt(match.group(2));
                    long delayMS = Long.parseLong(match.group(3));
                    int foAmount = Integer.parseInt(match.group(4));
                    System.out.printf("Packet info:\n" +
                            "\tcurrentTick: %d\n" +
                            "\tdelay: %d ms\n" +
                            "\tlastPacketReceivedTick: %d\n" +
                            "\tobjAmount: %d\n", currentTick, delayMS, lastPacketReceivedTick, foAmount);
                    val foDescriptions = new String[foAmount];
                    System.out.println("Receiving packets... ");
                    for (int i = 0; i < foAmount; i++)
                    //noinspection StatementWithEmptyBody
                    {
                        while ((foDescriptions[i] = in.readLine()) == null) {
                        }
                        System.out.println("Packet #" + i + ": " + foDescriptions[i]);
                    }
                    game.updateField(currentTick, foDescriptions);
                    SwingUtilities.updateComponentTreeUI(frame);
                }
            } catch (IOException e) {
                e.printStackTrace();
                closeAll();
            }
        }).start();
    }

    private void closeAll() {
        try {
            if (in != null)
                in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (out != null)
            out.close();
        while (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Direction getCurrentDirection() {
        return game.getCurrentDirection();
    }

    @Override
    public void setCurrentDirection(Direction direction) {
        game.setCurrentDirection(direction);
        sendDirectionUpdatePacket();
    }

    private void sendDirectionUpdatePacket() {
        sendDirectionUpdatePacket(game.getCurrentDirection());
    }

    private void sendDirectionUpdatePacket(Direction direction) {
        val message = game.getTime() + ":" + direction;
        System.out.println("Sending DirectionUpdatePacket: " + message);
        out.println(message);
    }


    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        closeAll();
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
