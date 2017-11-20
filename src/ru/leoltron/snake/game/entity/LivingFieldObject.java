package ru.leoltron.snake.game.entity;

import ru.leoltron.snake.game.GameField;
import ru.leoltron.snake.util.GamePoint;

public abstract class LivingFieldObject extends FieldObject {
    public abstract void tick(GameField field, GamePoint point);
}
