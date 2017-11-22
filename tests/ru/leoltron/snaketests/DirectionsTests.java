package ru.leoltron.snaketests;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.util.GamePoint;

import java.awt.*;

public class DirectionsTests extends Assert {
    @Test
    public void testTranslationUp() {
        val point = new GamePoint(5, 5);
        assertEquals(point.translated(Direction.UP), new GamePoint(5, 4));
    }

    @Test
    public void testTranslationDown() {
        val point = new GamePoint(5, 5);
        assertEquals(point.translated(Direction.DOWN), new GamePoint(5, 6));
    }

    @Test
    public void testTranslationLeft() {
        val point = new GamePoint(5, 5);
        assertEquals(point.translated(Direction.LEFT), new GamePoint(4, 5));
    }

    @Test
    public void testTranslationRight() {
        val point = new GamePoint(new Point(5, 5));
        assertEquals(point.translated(Direction.RIGHT), new GamePoint(6, 5));
    }
}
