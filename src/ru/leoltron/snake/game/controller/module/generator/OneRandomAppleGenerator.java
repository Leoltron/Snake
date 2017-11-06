package ru.leoltron.snake.game.controller.module.generator;

import ru.leoltron.snake.game.GameField;
import ru.leoltron.snake.game.entity.Apple;

public class OneRandomAppleGenerator implements AppleGenerator {
    private Apple apple;

    @Override
    public void onStartNewGame(GameField field) {
        apple = null;
        tick(field);
    }

    @Override
    public void tick(GameField field) {
        if (apple == null || apple.isDead())
            field.addEntity(field.getRandomFreeLocation(), apple = new Apple());
    }
}