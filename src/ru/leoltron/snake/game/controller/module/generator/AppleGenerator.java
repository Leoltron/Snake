package ru.leoltron.snake.game.controller.module.generator;

import ru.leoltron.snake.game.GameField;

public interface AppleGenerator {
    void onStartNewGame(GameField field);

    void tick(GameField field);
}
