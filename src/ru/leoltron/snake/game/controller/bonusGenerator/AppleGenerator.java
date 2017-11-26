package ru.leoltron.snake.game.controller.bonusGenerator;

import ru.leoltron.snake.game.GameField;

public interface AppleGenerator {
    void onStartNewGame(GameField field);

    void tick(GameField field);
}
