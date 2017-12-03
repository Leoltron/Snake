package ru.leoltron.snake;

import lombok.val;
import ru.leoltron.snake.game.Game;
import ru.leoltron.snake.game.controller.AdaptedMultiLevelGameController;
import ru.leoltron.snake.game.controller.snake.SimpleAISnakeController;
import ru.leoltron.snake.game.controller.snake.SnakeController;
import ru.leoltron.snake.gui.GameFrame;
import ru.leoltron.snake.gui.GameKeyListener;
import ru.leoltron.snake.gui.SelectModeFrame;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        new SelectModeFrame();
    }

    private static void startSinglePlayerGame() throws IOException {
        val scale = 0.25d;

        val fieldWidth = 64;
        val fieldHeight = 32;

        val controller1 = new SnakeController(0);
        val controller2 = new SnakeController(1);
        val game = new Game(new AdaptedMultiLevelGameController(controller1, controller2), fieldWidth, fieldHeight);
        val frame = new GameFrame(game, scale,
                new GameKeyListener(controller1, 0),
                new GameKeyListener(controller2, 1));
        game.startNewGame();

        frame.addKeyListener(getLevelKeyListener(game, frame));

        val period = 300;
        val timer = new Timer(period, actionEvent -> tickGameAndUpdate(game, frame, false));
        timer.start();
    }

    private static void startDoublePlayerGame() throws IOException {
        val scale = 0.25d;

        val fieldWidth = 64;
        val fieldHeight = 32;

        val controller1 = new SnakeController(0);
        val controller2 = new SimpleAISnakeController(1);
        val game = new Game(new AdaptedMultiLevelGameController(controller1, controller2), fieldWidth, fieldHeight);
        val frame = new GameFrame(game, scale, new GameKeyListener(controller1, 0));
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
}