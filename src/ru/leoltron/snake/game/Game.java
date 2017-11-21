package ru.leoltron.snake.game;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import ru.leoltron.snake.game.controller.GameController;
import ru.leoltron.snake.game.controller.MultiLevelGameController;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.game.entity.FieldObjectMoving;
import ru.leoltron.snake.game.entity.LivingFieldObject;
import ru.leoltron.snake.util.GamePoint;
import ru.leoltron.snake.util.Pair;

import java.util.*;

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
            Collection<Map.Entry<GamePoint, LivingFieldObject>> entries = gameField.getLivingFieldObjects();
            val pairs = new ArrayList<Pair<GamePoint, LivingFieldObject>>(entries.size());
            for (val entry : entries)
                pairs.add(Pair.create(entry.getKey(), entry.getValue()));

            for (val pair : pairs)
                pair.getItem2().tick(gameField, pair.getItem1());
//            val movedObjects = moveFieldObjects();
//            for (val entry : movedObjects)
//                gameField.addEntity(entry.getItem1(), entry.getItem2());
            gameController.tick();
        }
        time++;
    }

    private List<Pair<GamePoint, FieldObject>> moveFieldObjects() {
        val movedObjects = new ArrayList<Pair<GamePoint, FieldObject>>();

        Iterator<Map.Entry<GamePoint, FieldObject>> iterator = gameField.getFieldObjects().iterator();
        while (iterator.hasNext()) {
            Map.Entry<GamePoint, FieldObject> entry = iterator.next();
            val fieldObject = entry.getValue();
            if (fieldObject instanceof FieldObjectMoving) {
                int x = entry.getKey().x;
                int y = entry.getKey().y;

                val movingObject = ((FieldObjectMoving) fieldObject);
                x += movingObject.getVelX();
                y += movingObject.getVelY();

                movedObjects.add(Pair.create(new GamePoint(x, y), fieldObject));
                iterator.remove();
            }
            fieldObject.tick();
        }
        return movedObjects;
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

