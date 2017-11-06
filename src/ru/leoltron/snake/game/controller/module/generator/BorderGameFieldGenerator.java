package ru.leoltron.snake.game.controller.module.generator;

import lombok.val;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.game.entity.Wall;
import ru.leoltron.snake.util.GamePoint;

import java.util.HashMap;
import java.util.Map;

public class BorderGameFieldGenerator extends GameFieldGenerator {
    @Override
    public Map<GamePoint, FieldObject> generateFieldObjects(int width, int height) {
        val objects = new HashMap<GamePoint, FieldObject>();
        val top = 0;
        val bottom = height - 1;
        val left = 0;
        val right = width - 1;

        for (int y = top; y <= bottom; y++) {
            addWallAt(objects, left, y);
            addWallAt(objects, right, y);
        }

        for (int x = left + 1; x < right; x++) {
            addWallAt(objects, x, top);
            addWallAt(objects, x, bottom);
        }
        return objects;
    }

    private static void addWallAt(Map<GamePoint, FieldObject> objectMap, int x, int y) {
        objectMap.put(new GamePoint(x, y), new Wall());
    }
}
