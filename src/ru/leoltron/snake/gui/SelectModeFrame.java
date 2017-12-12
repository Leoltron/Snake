package ru.leoltron.snake.gui;

import lombok.val;
import ru.leoltron.snake.game.Game;
import ru.leoltron.snake.network.MPClient;
import ru.leoltron.snake.network.MPServer;
import ru.leoltron.snake.util.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class SelectModeFrame extends JFrame {

    private static final Pattern HOST_PORT_PATTERN = Pattern.compile("([^:]+):([\\d]+)");

    private JPanel btnPanel;

    public SelectModeFrame() {
        super("Snake");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        try {
            this.setIconImage(ImageIO.read(new File(String.join(File.separator,
                    "resources", "textures", "snake", "green", "head.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(3, 1, 5, 3));
        addWithListener(new JButton("Одиночная игра"), e -> new SinglePlayerSettingsFrame(this).setVisible(true));
        addWithListener(new JButton("Мультиплеер (Создать)"), e -> startMultiPlayerServer());
        addWithListener(new JButton("Мультиплеер (Присоединиться)"), e -> joinMultiplayer());
        setContentPane(btnPanel);
        pack();
        setMinimumSize(getSize());
        this.setLocationRelativeTo(null);
    }

    static KeyListener getLevelKeyListener(Game game, GameFrame frame) {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_G)
                    frame.gamePanel.switchGridDrawMode();
                else if (e.getKeyCode() == KeyEvent.VK_SPACE)
                    tickGameAndUpdate(game, frame, true);
                else if (e.isControlDown()) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_A:
                            game.goToPrevLevel();
                            break;
                        case KeyEvent.VK_D:
                            game.goToNextLevel();
                            break;
                        case KeyEvent.VK_L:
                            game.restartLevel();
                            break;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
    }

    private static boolean isValidPort(String s) {
        return Pattern.matches("[\\d]+", s) && isValidPort(Integer.parseInt(s));
    }

    private static boolean isValidPort(int i) {
        return i >= 0 && i <= 65535;
    }

    private void joinMultiplayer() {
        String result = JOptionPane.showInputDialog(btnPanel,
                "Укажите адрес хоста", "Мультиплеер (Присоединитсья)", JOptionPane.PLAIN_MESSAGE);
        val pair = tryExtractHostnameAndPort(result);
        if (pair == null) {
            JOptionPane.showMessageDialog(btnPanel,
                    "Неверный формат, адрес должен быть указан в формате\nимя_хоста:порт, порт должен быть числом от 1 до 65535",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        } else
            new MPClient(this, btnPanel, pair.getItem1(), pair.getItem2());
    }

    private void startMultiPlayerServer() {
        String result = JOptionPane.showInputDialog(btnPanel,
                "Укажите порт", "Мультиплеер (Создать)", JOptionPane.PLAIN_MESSAGE);
        if (!isValidPort(result)) {
            JOptionPane.showMessageDialog(btnPanel,
                    "Порт должен быть числом от 1 до 65535",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        } else {
            this.setVisible(false);
            new MPServer(Integer.parseInt(result));
        }
    }

    private Pair<String, Integer> tryExtractHostnameAndPort(String string) {
        val match = HOST_PORT_PATTERN.matcher(string);
        if (!match.matches())
            return null;
        else {
            val port = Integer.parseInt(match.group(2));
            return isValidPort(port) ? Pair.create(match.group(1), port) : null;
        }
    }

    static void tickGameAndUpdate(Game game, JFrame frame, boolean forceTick) {
        game.tick(forceTick);
        SwingUtilities.updateComponentTreeUI(frame);
    }

    private void addWithListener(JButton button, ActionListener listener) {
        btnPanel.add(button);
        button.addActionListener(listener);
    }

}
