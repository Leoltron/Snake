package ru.leoltron.snake;

import lombok.val;
import ru.leoltron.snake.game.Game;
import ru.leoltron.snake.game.controller.AdaptedMultiLevelGameController;
import ru.leoltron.snake.game.controller.snake.SimpleAISnakeController;
import ru.leoltron.snake.game.controller.snake.SnakeController;
import ru.leoltron.snake.gui.GameKeyListener;
import ru.leoltron.snake.gui.GamePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

public class Main {
    private static void setFrame(JFrame frame,
                                 GamePanel gamePanel,
                                 int panelWidth,
                                 int panelHeight,
                                 KeyListener... listeners) {
        frame.add(gamePanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
        frame.getContentPane().setPreferredSize(new Dimension(panelWidth, panelHeight));
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        for (val listener : listeners)
            frame.addKeyListener(listener);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Snake");
        try {
            frame.setIconImage(ImageIO.read(new File(String.join(File.separator,
                    "resources", "textures", "snake", "green", "head.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        startDoublePlayerGame();
    }

    private static void startSinglePlayerGame() throws IOException {
        val scale = 0.25d;

        val fieldWidth = 64;
        val fieldHeight = 32;
        val panelWidth = (int) (fieldWidth * 64 * scale);
        val panelHeight = (int) (fieldHeight * 64 * scale);

        val controller = new SnakeController(0);
        val game = new Game(new AdaptedMultiLevelGameController(controller), fieldWidth, fieldHeight);
        game.startNewGame();
        val gui = new GamePanel(fieldWidth, fieldHeight, game, scale);
        val frame = new JFrame();

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_G)
                    gui.switchGridDrawMode();
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
        });

        setFrame(frame, gui, panelWidth, panelHeight, new GameKeyListener(controller, 0));
        val period = 300;
        val timer = new Timer(period, actionEvent -> tickGameAndUpdate(game, frame, false));
        timer.start();
    }

    private static void startDoublePlayerGame() throws IOException {
        val scale = 0.25d;

        val fieldWidth = 64;
        val fieldHeight = 32;
        val panelWidth = (int) (fieldWidth * 64 * scale);
        val panelHeight = (int) (fieldHeight * 64 * scale);

        val controller1 = new SnakeController(0);
        val controller2 = new SimpleAISnakeController(1);
        val game = new Game(new AdaptedMultiLevelGameController(controller1, controller2), fieldWidth, fieldHeight);
        game.startNewGame();
        val gui = new GamePanel(fieldWidth, fieldHeight, game, scale);
        val frame = new JFrame();

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_G)
                    gui.switchGridDrawMode();
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
        });

        setFrame(frame, gui, panelWidth, panelHeight, new GameKeyListener(controller1, 0), new GameKeyListener(controller2, 1));
        val period = 300;
        val timer = new Timer(period, actionEvent -> tickGameAndUpdate(game, frame, false));
        timer.start();
    }

    private static void tickGameAndUpdate(Game game, JFrame frame, boolean forceTick) {
        game.tick(forceTick);
        SwingUtilities.updateComponentTreeUI(frame);
    }
}