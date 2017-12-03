package ru.leoltron.snake.gui;

import lombok.val;
import ru.leoltron.snake.game.Game;
import ru.leoltron.snake.game.controller.AdaptedMultiLevelGameController;
import ru.leoltron.snake.game.controller.snake.SimpleAISnakeController;
import ru.leoltron.snake.game.controller.snake.SnakeController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

public class SelectModeFrame extends JFrame {

    private JButton singlePlayerButton;
    private JButton singlePlayerAndAIButton;
    private JButton multiPlayerButton;

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
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 5, 3));
        panel.add(singlePlayerButton = new JButton("Одиночная игра"));
        singlePlayerButton.addActionListener(e -> startSinglePlayerGame());
        panel.add(singlePlayerAndAIButton = new JButton("Одиночная игра с компьютером"));
        singlePlayerAndAIButton.addActionListener(e -> startDoublePlayerGame());
        panel.add(multiPlayerButton = new JButton("Мультиплеер"));
        setContentPane(panel);
        pack();
        setMinimumSize(getSize());
        this.setLocationRelativeTo(null);
    }

    private static void startSinglePlayerGame(JFrame parent) throws IOException {
        val scale = 0.25d;

        val fieldWidth = 64;
        val fieldHeight = 32;

        val controller1 = new SnakeController(0);
        val controller2 = new SnakeController(1);
        val game = new Game(new AdaptedMultiLevelGameController(controller1, controller2), fieldWidth, fieldHeight);
        val frame = new GameFrame(game, scale,
                new GameKeyListener(controller1, 0),
                new GameKeyListener(controller2, 1));
        if (parent != null)
            frame.setParentFrame(parent);
        game.startNewGame();

        frame.addKeyListener(getLevelKeyListener(game, frame));

        val period = 300;
        val timer = new Timer(period, actionEvent -> tickGameAndUpdate(game, frame, false));
        timer.start();
    }

    private static void startDoublePlayerGame(JFrame parent) throws IOException {
        val scale = 0.25d;

        val fieldWidth = 64;
        val fieldHeight = 32;

        val controller1 = new SnakeController(0);
        val controller2 = new SimpleAISnakeController(1);
        val game = new Game(new AdaptedMultiLevelGameController(controller1, controller2), fieldWidth, fieldHeight);
        val frame = new GameFrame(game, scale, new GameKeyListener(controller1, 0));
        if (parent != null)
            frame.setParentFrame(parent);
        game.startNewGame();

        frame.addKeyListener(getLevelKeyListener(game, frame));

        val period = 300;
        val timer = new Timer(period, actionEvent -> tickGameAndUpdate(game, frame, false));
        timer.start();
    }

    private static KeyListener getLevelKeyListener(Game game, GameFrame frame) {
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

    private static void tickGameAndUpdate(Game game, JFrame frame, boolean forceTick) {
        game.tick(forceTick);
        SwingUtilities.updateComponentTreeUI(frame);
    }

    private void startSinglePlayerGame() {
        try {
            startSinglePlayerGame(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startDoublePlayerGame() {
        setVisible(false);
        try {
            startDoublePlayerGame(this);
        } catch (IOException e) {
            e.printStackTrace();
            setVisible(true);
        }
    }
}
