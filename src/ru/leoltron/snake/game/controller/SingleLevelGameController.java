package ru.leoltron.snake.game.controller;

import lombok.NonNull;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.GameField;
import ru.leoltron.snake.game.controller.module.SnakeController;
import ru.leoltron.snake.game.controller.module.generator.*;

public class SingleLevelGameController implements GameController {
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
    public void setCurrentDirection(Direction direction) {
        snakeController.setCurrentDirection(direction);
    }

    @Override
    public boolean isSnakeDead() {
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


    public static SingleLevelGameController getClassicGameController() {
        return new SingleLevelGameController(
                new RandomApplesGenerator(),
                new BorderGameFieldGenerator(),
                new SnakeController());
    }

    public static SingleLevelGameController getPredefinedLevelGameController(String level) {
        return new SingleLevelGameController(
                new RandomApplesGenerator(),
                new PredefinedFieldGenerator(level),
                new SnakeController());
    }

    public static SingleLevelGameController getPredefinedLevelGameController(String[] level) {
        return new SingleLevelGameController(
                new RandomApplesGenerator(),
                new PredefinedFieldGenerator(level),
                new SnakeController());
    }
}
