package ru.leoltron.snake.game.controller.snake;

import ru.leoltron.snake.game.GameField;

public abstract class AISnakeController extends SnakeController {
    public AISnakeController(int playerId) {
        super(playerId);
    }

    public AISnakeController(int playerId, int snakeLength) {
        super(playerId, snakeLength);
    }

    public abstract void preTick(GameField field);
}
