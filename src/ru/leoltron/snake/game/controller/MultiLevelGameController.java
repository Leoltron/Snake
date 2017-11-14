package ru.leoltron.snake.game.controller;

import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.GameField;
import ru.leoltron.snake.game.controller.module.SnakeController;
import ru.leoltron.snake.game.controller.module.generator.*;

import java.io.IOException;
import java.util.Random;

public class MultiLevelGameController implements GameController {
    private static final int APPLES_REQUIRED_TO_EAT_BEFORE_NEXT_LEVEL = 4;

    private final Random rand = new Random();
    private int level;
    private int appleEaten;
    private GameField field;
    private AppleGenerator appleGenerator;
    private SnakeController snakeController;
    private boolean isStartLevelPauseActive = true;

    public MultiLevelGameController() {
        appleGenerator = new RandomApplesGenerator();
        snakeController = new SnakeController() {
            @Override
            public void onAppleEaten() {
                super.onAppleEaten();

                appleEaten++;
                if (appleEaten >= APPLES_REQUIRED_TO_EAT_BEFORE_NEXT_LEVEL)
                    moveToNextLevel();
            }
        };
    }

    @Override
    public void tick() {
        appleGenerator.tick(field);
        snakeController.tick(field);
    }

    @Override
    public void startNewGame(GameField field) {
        this.field = field;
        level = -1;
        moveToNextLevel();
    }

    private void moveToNextLevel() {
        isStartLevelPauseActive = true;
        level++;
        appleEaten = 0;
        GameFieldGenerator generator;
        if (level < 2)
            try {
                generator = PredefinedFieldGenerator.fromFile("resources/levels/level" + (level + 1) + ".txt");
            } catch (IOException e) {
                e.printStackTrace();
                generator = new RandomGameFieldGenerator();
            }
        else
            generator = new RandomGameFieldGenerator();
        generator.generateFieldObjects(field);
        appleGenerator.onStartNewGame(field);
        respawnSnakeAtRandomLocation();
    }

    private void respawnSnakeAtRandomLocation() {
        val allFreeLocations = field.getAllFreeLocations();
        while (!allFreeLocations.isEmpty()) {
            val point = allFreeLocations.remove(rand.nextInt(allFreeLocations.size()));
            for (val direction : Direction.values())
                if (field.isFree(point.translated(direction))
                        && field.isFree(point.translated(direction).translated(direction))) {
                    snakeController.setStartPoint(point);
                    snakeController.respawnSnake(field);
                    snakeController.setCurrentDirection(direction);
                    break;
                }
        }
    }

    @Override
    public void setCurrentDirection(Direction direction) {
        snakeController.setCurrentDirection(direction);
    }

    @Override
    public boolean isSnakeDead() {
        return snakeController.isSnakeDead(field);
    }

    @Override
    public boolean isTempPaused() {
        return isStartLevelPauseActive;
    }

    @Override
    public void setTempUnpaused() {
        isStartLevelPauseActive = false;
    }
}
