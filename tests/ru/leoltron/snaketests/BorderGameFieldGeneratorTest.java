package ru.leoltron.snaketests;

import lombok.val;
import org.junit.Test;
import ru.leoltron.snake.game.controller.fieldGenerator.BorderGameFieldGenerator;
import ru.leoltron.snake.game.entity.Wall;

import static org.junit.Assert.*;

public class BorderGameFieldGeneratorTest {
    @Test
    public void generateFieldObjects() throws Exception {
        val result = new BorderGameFieldGenerator().generateFieldObjects(3, 3);
        for (val pair: result.entrySet()){
            if (pair.getKey().x == 1 && pair.getKey().y == 1)
                assertFalse(pair.getValue() instanceof Wall);
            else
                assertTrue(pair.getValue() instanceof Wall);
        }
    }

}