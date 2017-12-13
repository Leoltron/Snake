package ru.leoltron.snaketests;

import lombok.val;
import org.junit.Test;
import ru.leoltron.snake.game.controller.fieldGenerator.PredefinedFieldGenerator;
import ru.leoltron.snake.game.entity.Apple;
import ru.leoltron.snake.game.entity.AppleEater;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.game.entity.Wall;
import ru.leoltron.snake.util.GamePoint;

import java.util.HashMap;

import static org.junit.Assert.*;

public class PredefinedFieldGeneratorTest {

    @Test
    public void generateFieldObjectsFromString() throws Exception {
        val generator = new PredefinedFieldGenerator("WWWW\nWAE");
        val result = generator.generateFieldObjects(4, 2);
        for (val pair: result.entrySet()){
            if (pair.getKey().y == 0 || pair.getKey().x == 0)
                assertTrue(pair.getValue() instanceof Wall);
            else if (pair.getKey().x == 2)
                assertTrue(pair.getValue() instanceof AppleEater);
            else assertTrue(pair.getValue() instanceof Apple);
        }
    }



}