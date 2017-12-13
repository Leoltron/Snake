package ru.leoltron.snaketests;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import ru.leoltron.snake.game.controller.bonusGenerator.RandomApplesGenerator;
import ru.leoltron.snake.game.entity.Apple;
import ru.leoltron.snake.game.field.GameField;

import static org.junit.Assert.*;

public class RandomApplesGeneratorTest {
    private GameField field;
    @Before
    public void init(){
        field = new GameField(2, 2);
    }

    public int getAppleCount(){
        int cnt = 0;
        for (val pair: field.getFieldObjects())
            cnt += pair.getValue().getClass() == Apple.class ? 1 : 0;
        return cnt;
    }

    @Test
    public void testStartNewGame(){
        val appleGenerator = new RandomApplesGenerator(2);
        appleGenerator.onStartNewGame(field);
        assertEquals(2, getAppleCount());
    }

    @Test
    public void testTick(){
        val appleGenerator = new RandomApplesGenerator();
        appleGenerator.tick(field);
        appleGenerator.tick(field);
        appleGenerator.tick(field);
        assertEquals(1, getAppleCount());
    }
}