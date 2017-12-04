package ru.leoltron.snake.game.controller.fieldGenerator;

import lombok.val;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.game.field.GameField;
import ru.leoltron.snake.util.GamePoint;

import java.util.Map;

public abstract class GameFieldGenerator {
    public void generateFieldObjects(GameField gameField) {
        gameField.clear();
        for (val entry : generateFieldObjects(gameField.getFieldWidth(), gameField.getFieldHeight()).entrySet())
            gameField.addEntity(entry.getKey(), entry.getValue());
    }

    protected abstract Map<GamePoint, FieldObject> generateFieldObjects(int fieldWidth, int fieldHeight);

}
