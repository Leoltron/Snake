package ru.leoltron.snake.gui;

import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.controller.snake.SnakeController;
import ru.leoltron.snake.util.MapUtils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.leoltron.snake.game.Direction.*;

public class GameKeyListener implements KeyListener {
    private static final List<Map<Integer, Direction>> CONTROL_KEY_BINDINGS = new ArrayList<>();

    static {
        val player1Bindings = new HashMap<Integer, Direction>();
        MapUtils.fillMap(player1Bindings,
                KeyEvent.VK_UP, UP,
                KeyEvent.VK_DOWN, DOWN,
                KeyEvent.VK_LEFT, LEFT,
                KeyEvent.VK_RIGHT, RIGHT);
        CONTROL_KEY_BINDINGS.add(player1Bindings);

        val player2Bindings = new HashMap<Integer, Direction>();
        MapUtils.fillMap(player2Bindings,
                KeyEvent.VK_W, UP,
                KeyEvent.VK_S, DOWN,
                KeyEvent.VK_A, LEFT,
                KeyEvent.VK_D, RIGHT);
        CONTROL_KEY_BINDINGS.add(player2Bindings);
    }

    private SnakeController controller;
    private Map<Integer, Direction> keyBindings;

    public GameKeyListener(SnakeController controller, int usingControlSet) {
        this.controller = controller;
        this.keyBindings = CONTROL_KEY_BINDINGS.get(usingControlSet);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        val direction = keyBindings.getOrDefault(e.getKeyCode(), null);
        if (direction != null)
            controller.setCurrentDirection(direction);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
