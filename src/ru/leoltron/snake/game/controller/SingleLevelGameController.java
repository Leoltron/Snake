package ru.leoltron.snake.game.controller;

import lombok.NonNull;
import ru.leoltron.snake.game.GameField;
import ru.leoltron.snake.game.controller.bonusGenerator.AppleGenerator;
import ru.leoltron.snake.game.controller.fieldGenerator.GameFieldGenerator;
import ru.leoltron.snake.game.controller.snake.SnakeController;

public class SingleLevelGameController extends GameController {
    private final GameFieldGenerator gameFieldGenerator;
    private final AppleGenerator appleGenerator;
    private final SnakeController snakeController;

    private GameField gameField;

    private boolean isStartGamePauseActive = false;

    public SingleLevelGameController(@NonNull AppleGenerator appleGenerator,
                                     @NonNull GameFieldGenerator gameFieldGenerator,
                                     @NonNull SnakeController snakeController) {
        this.appleGenerator = appleGenerator;
        this.gameFieldGenerator = gameFieldGenerator;
        this.snakeController = snakeController;
    }

    public SingleLevelGameController setStartGamePauseActive() {
        isStartGamePauseActive = true;
        return this;
    }

    @Override
    public void startNewGame(GameField field) {
        gameField = field;

        gameFieldGenerator.generateFieldObjects(gameField);
        appleGenerator.onStartNewGame(gameField);
        snakeController.respawnSnake(gameField);
    }

    @Override
    public void tick() {
        appleGenerator.tick(gameField);
        snakeController.tick(gameField);
    }

    @Override
    public boolean isGameOver() {
        return snakeController.isSnakeDead(gameField);
    }

    @Override
    public boolean isTempPaused() {
        return isStartGamePauseActive;
    }

    @Override
    public void setTempUnpaused() {
        isStartGamePauseActive = false;
    }
}
