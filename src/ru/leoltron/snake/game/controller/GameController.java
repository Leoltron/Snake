package ru.leoltron.snake.game.controller;

import ru.leoltron.snake.game.field.GameField;

public abstract class GameController {
    public abstract void tick();

    public abstract void startNewGame(GameField field);

    public abstract boolean isGameOver();

    public abstract boolean isTempPaused();

    public abstract void setTempUnpaused();
}
