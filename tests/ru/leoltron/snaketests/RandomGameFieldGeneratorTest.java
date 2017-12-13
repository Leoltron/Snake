package ru.leoltron.snaketests;

import lombok.val;
import org.junit.Test;
import ru.leoltron.snake.game.controller.fieldGenerator.RandomGameFieldGenerator;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.game.field.GameField;
import ru.leoltron.snake.util.GamePoint;
import ru.leoltron.snake.util.algorithms.Algorithms;
import ru.leoltron.snake.util.algorithms.GameFieldUtil;

import java.util.Map;

import static org.junit.Assert.*;

public class RandomGameFieldGeneratorTest {
    @Test
    public void generateFieldObjects() throws Exception {
        for (int i = 0; i < 1000; i++)
            assertTrue(test());
    }

    private boolean test(){
        val generator = new RandomGameFieldGenerator(5, 15);
        val objects = generator.generateFieldObjects(10, 10);
        val field = generateField(objects);
        val graph = GameFieldUtil.buildGraph(field);
        return Algorithms.getBridges(graph).isEmpty();
    }

    private GameField generateField(Map<GamePoint, FieldObject> objects) {
        GameField field = new GameField(10, 10);
        for (val pair: objects.entrySet())
            field.addEntity(pair.getKey(), pair.getValue());
        return field;
    }

}