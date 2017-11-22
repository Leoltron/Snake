package ru.leoltron.snake.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import ru.leoltron.snake.game.controller.GameController;
import ru.leoltron.snake.game.controller.MultiLevelGameController;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.game.entity.LivingFieldObject;
import ru.leoltron.snake.util.GamePoint;
import ru.leoltron.snake.util.Pair;

import java.util.Collection;

public class Game {

    public static final int TEMP_PAUSE_TIME_TICKS = 3;
    @Getter
    private int time;

    @Getter
    private boolean isPaused;

    @Getter
    private int tempPauseTime = 0;

    public final GameController gameController;
    private GameField gameField;

    public Game(@NonNull GameController gameController,
                int fieldWidth, int fieldHeight) {
        this.gameController = gameController;
        gameField = new GameField(fieldWidth, fieldHeight);
    }


    public void startNewGame() {
        gameController.startNewGame(gameField);
        time = 0;
        isPaused = false;
    }

    public void restartLevel() {
        if (this.gameController instanceof MultiLevelGameController) {
            val mlGameController = (MultiLevelGameController) this.gameController;
            mlGameController.moveToLevel(mlGameController.getLevelNumber());
        } else
            startNewGame();
    }

    public void goToNextLevel() {
        if (this.gameController instanceof MultiLevelGameController) {
            val mlGameController = (MultiLevelGameController) this.gameController;
            mlGameController.moveToLevel(mlGameController.getLevelNumber() + 1);
        } else
            startNewGame();
    }

    public void goToPrevLevel() {
        if (this.gameController instanceof MultiLevelGameController) {
            val mlGameController = (MultiLevelGameController) this.gameController;
            mlGameController.moveToLevel(mlGameController.getLevelNumber() - 1);
        } else
            startNewGame();
    }

    public void tick() {
        tick(true);
    }

    public void tick(boolean forced) {
        if (isGameOver()) return;

        if (gameController.isTempPaused()) {
            tempPauseTime = TEMP_PAUSE_TIME_TICKS;
            gameController.setTempUnpaused();
        } else if (tempPauseTime > 0) {
            if (!forced)
                tempPauseTime--;
            return;
        } else if (!isPaused) {
            Collection<Pair<GamePoint, LivingFieldObject>> pairs = gameField.getLivingFieldObjects();
            for (val pair : pairs)
                pair.getItem2().tick(gameField, pair.getItem1());
            gameController.tick();
        }
        time++;
    }

    public void setCurrentDirection(Direction direction) {
        gameController.setCurrentDirection(direction);
    }

    public boolean isGameOver() {
        return gameController.isSnakeDead();
    }

    public FieldObject getObjectAt(int x, int y) {
        return gameField.getObjectAt(x, y);
    }

    public void switchPaused() {
        isPaused = !isPaused;
    }
}

