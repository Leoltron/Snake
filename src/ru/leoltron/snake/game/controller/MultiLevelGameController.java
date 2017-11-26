package ru.leoltron.snake.game.controller;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.GameField;
import ru.leoltron.snake.game.controller.bonusGenerator.AppleGenerator;
import ru.leoltron.snake.game.controller.bonusGenerator.RandomApplesGenerator;
import ru.leoltron.snake.game.controller.fieldGenerator.PredefinedFieldGenerator;
import ru.leoltron.snake.game.controller.fieldGenerator.RandomGameFieldGenerator;
import ru.leoltron.snake.game.controller.snake.SnakeController;
import ru.leoltron.snake.game.entity.Edible;
import ru.leoltron.snake.util.GamePoint;

import java.io.IOException;
import java.util.*;

public class MultiLevelGameController extends GameController {
    private int applesRequiredForLevel = 4;

    private final Random rand = new Random();
    @Getter
    private int levelNumber;
    private int applesEaten;
    private GameField field;
    private AppleGenerator appleGenerator;
    private List<SnakeController> players;
    private boolean isStartLevelPauseActive = true;

    private Map<Integer, Level> levels = new HashMap<>();
    private Level defaultLevel = new Level(new RandomGameFieldGenerator(), new RandomApplesGenerator());

    public MultiLevelGameController(SnakeController controller) {
        this(Collections.singletonList(controller));
    }

    public MultiLevelGameController(List<SnakeController> controllers) {
        this.players = new ArrayList<>(controllers.size());
        for (val snakeController : controllers)
            this.players.add(new SnakeControllerAdapter(snakeController));
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
        for (val snakeController : players)
            snakeController.tick(field);
    }

    @Override
    public void startNewGame(GameField field) {
        this.field = field;
        levelNumber = 0;
        moveToNextLevel();
    }

    public void moveToLevel(int level) {
        levelNumber = level - 1;
        moveToNextLevel();
    }

    private void moveToNextLevel() {
        isStartLevelPauseActive = true;
        levelNumber++;
        applesEaten = 0;
        startLevel();
        for (val player : players)
            respawnSnakeAtRandomLocation(player);
    }

    private void startLevel() {
        val level = levels.getOrDefault(levelNumber, defaultLevel);
        applesRequiredForLevel = level.applesInLevel;
        level.fieldGenerator.generateFieldObjects(field);
        appleGenerator = level.appleGenerator;
        appleGenerator.onStartNewGame(field);

    }

    private void respawnSnakeAtRandomLocation(SnakeController snakeController) {
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
    public boolean isGameOver() {
        for (val snakeController : players)
            if (!snakeController.isSnakeDead(field))
                return false;

        return true;
    }

    @Override
    public boolean isTempPaused() {
        return isStartLevelPauseActive;
    }

    @Override
    public void setTempUnpaused() {
        isStartLevelPauseActive = false;
    }

    public int getApplesLeftToEat() {
        return applesRequiredForLevel - applesEaten;
    }


    class SnakeControllerAdapter extends SnakeController {
        private final SnakeController snakeController;

        SnakeControllerAdapter(SnakeController snakeController) {
            super(snakeController.getPlayerId());
            this.snakeController = snakeController;
        }

        @Override
        public void setStartPoint(GamePoint startPoint) {
            snakeController.setStartPoint(startPoint);
        }

        @Override
        public void setStartDirection(Direction startDirection) {
            snakeController.setStartDirection(startDirection);
        }

        @Override
        public void onFoodEaten(Edible edible) {
            snakeController.onFoodEaten(edible);

            applesEaten += edible.getFoodValue();
            if (applesEaten >= applesRequiredForLevel)
                moveToNextLevel();
        }

        @Override
        public void tick(GameField field) {
            snakeController.tick(field);
        }

        @Override
        public void respawnSnake(GameField gameField) {
            snakeController.respawnSnake(gameField);
        }

        @Override
        public void setCurrentDirection(Direction direction) {
            snakeController.setCurrentDirection(direction);
        }

        @Override
        public boolean isSnakeDead(GameField field) {
            return snakeController.isSnakeDead(field);
        }
    }
}
