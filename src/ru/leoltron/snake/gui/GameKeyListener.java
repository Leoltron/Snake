package ru.leoltron.snake.gui;

import ru.leoltron.snake.game.Game;
import ru.leoltron.snake.game.controller.snake.SnakeController;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static ru.leoltron.snake.game.Direction.*;

public class GameKeyListener implements KeyListener {
    private Game game;
    private SnakeController controller;

    public GameKeyListener(Game game, SnakeController controller) {
        this.game = game;
        this.controller = controller;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                controller.setCurrentDirection(UP);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                controller.setCurrentDirection(DOWN);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                controller.setCurrentDirection(RIGHT);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                controller.setCurrentDirection(LEFT);
                break;
            case KeyEvent.VK_R:
                game.startNewGame();
                break;
            case KeyEvent.VK_P:
            case KeyEvent.VK_PAUSE:
                game.switchPaused();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
