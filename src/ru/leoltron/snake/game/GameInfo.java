package ru.leoltron.snake.game;

import ru.leoltron.snake.game.entity.FieldObject;

public interface GameInfo {
    boolean isGameOver();

    int getTempPauseTime();

    int getTime();

    boolean isPaused();

    FieldObject getObjectAt(int x, int y);

    int getFieldWidth();

    int getFieldHeight();
}
