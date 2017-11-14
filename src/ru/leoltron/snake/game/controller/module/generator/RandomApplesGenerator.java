package ru.leoltron.snake.game.controller.module.generator;

import ru.leoltron.snake.game.GameField;
import ru.leoltron.snake.game.entity.Apple;

public class RandomApplesGenerator implements AppleGenerator {
    protected Apple[] apples;

    public RandomApplesGenerator() {
        this(1);
    }

    public RandomApplesGenerator(int applesAmount) {
        apples = new Apple[applesAmount];
    }

    @Override
    public void onStartNewGame(GameField field) {
        for (int i = 0; i < apples.length; i++)
            apples[i] = null;
        tick(field);
    }

    @Override
    public void tick(GameField field) {
        for (int i = 0; i < apples.length; i++)
            if (apples[i] == null || apples[i].isDead())
                addNewApple(field, i);
    }

    protected void addNewApple(GameField field, int index) {
        field.addEntity(field.getRandomFreeLocation(), apples[index] = new Apple());
    }
}
