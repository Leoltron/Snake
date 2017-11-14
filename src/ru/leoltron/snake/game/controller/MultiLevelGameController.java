package ru.leoltron.snake.game.controller;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.GameField;
import ru.leoltron.snake.game.controller.module.Level;
import ru.leoltron.snake.game.controller.module.SnakeController;
import ru.leoltron.snake.game.controller.module.generator.AppleGenerator;
import ru.leoltron.snake.game.controller.module.generator.PredefinedFieldGenerator;
import ru.leoltron.snake.game.controller.module.generator.RandomApplesGenerator;
import ru.leoltron.snake.game.controller.module.generator.RandomGameFieldGenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MultiLevelGameController implements GameController {
    private int applesRequiredForLevel = 4;

    private final Random rand = new Random();
    @Getter
    private int levelNumber;
    private int appleEaten;
    private GameField field;
    private AppleGenerator appleGenerator;
    private SnakeController snakeController;
    private boolean isStartLevelPauseActive = true;

    private Map<Integer, Level> levels = new HashMap<>();
    private Level defaultLevel = new Level(new RandomGameFieldGenerator(), new RandomApplesGenerator());

    public MultiLevelGameController() {
        snakeController = new SnakeController() {
            @Override
            public void onAppleEaten() {
                super.onAppleEaten();

                appleEaten++;
                if (appleEaten >= applesRequiredForLevel)
                    moveToNextLevel();
            }
        };
        initLevels();
    }

    protected void initLevels() {
        try {
            levels.put(1, new Level(PredefinedFieldGenerator.fromFile("resources/fields/field1.txt"), new RandomApplesGenerator()));
            levels.put(2, new Level(PredefinedFieldGenerator.fromFile("resources/fields/field2.txt"), new RandomApplesGenerator()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setLevel(int levelNumber, @NonNull Level level) {
        levels.put(levelNumber, level);
    }

    void setDefaultLevel(@NonNull Level level) {
        defaultLevel = level;
    }

    @Override
    public void tick() {
        appleGenerator.tick(field);
        snakeController.tick(field);
    }

    @Override
    public void startNewGame(GameField field) {
        this.field = field;
        levelNumber = 0;
        moveToNextLevel();
    }

    private void moveToNextLevel() {
        isStartLevelPauseActive = true;
        levelNumber++;
        appleEaten = 0;
        startLevel();
        respawnSnakeAtRandomLocation();
    }

    private void startLevel() {
        val level = levels.getOrDefault(levelNumber, defaultLevel);
        applesRequiredForLevel = level.applesInLevel;
        level.fieldGenerator.generateFieldObjects(field);
        appleGenerator = level.appleGenerator;
        appleGenerator.onStartNewGame(field);

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
