package ru.leoltron.snake.game.controller;

import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.GameField;

public interface GameController {
    void tick();

    void startNewGame(GameField field);

    void setCurrentDirection(Direction direction);

    boolean isSnakeDead();
}
