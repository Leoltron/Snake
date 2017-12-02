package ru.leoltron.snake.game;

public interface LevelInfo {
    boolean isProvidingInfo();

    int getLevelNumber();

    int getApplesLeftToEat();
}
