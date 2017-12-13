package ru.leoltron.snaketests;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.Game;
import ru.leoltron.snake.util.GamePoint;

public class GamePointTests extends Assert {
    @Test
    public void testAdd() {
        val point1 = new GamePoint(1, 2);
        val point2 = new GamePoint(5, 7);
        val expected = new GamePoint(6, 9);
        assertEquals(expected, point1.add(point2));
    }

    @Test
    public void testManhattanDistance() {
        val point1 = new GamePoint(1, 2);
        val point2 = new GamePoint(5, 7);

        assertEquals(4 + 5, point1.manhattanDistanceTo(point2));
    }

    @Test
    public void testEuclideanDistance() {
        val point1 = new GamePoint(1, 2);
        val point2 = new GamePoint(5, 7);

        assertEquals(6.40312424, point1.euclideanDistanceTo(point2), 1e-5);
    }

    @Test
    public void testReversed() {
        val point = new GamePoint(1, 2);
        assertEquals(new GamePoint(-1, -2), point.reversed());
    }

    @Test
    public void testNormalized() {
        assertEquals(new GamePoint(1, 1), new GamePoint(10, 25).normalized());
        assertEquals(new GamePoint(1, 0), new GamePoint(10, 0).normalized());
        assertEquals(new GamePoint(1, -1), new GamePoint(10, -25).normalized());
        assertEquals(new GamePoint(0, 1), new GamePoint(0, 25).normalized());
        assertEquals(new GamePoint(0, 0), new GamePoint(0, 0).normalized());
        assertEquals(new GamePoint(0, -1), new GamePoint(0, -2).normalized());
        assertEquals(new GamePoint(-1, -1), new GamePoint(-3, -2).normalized());
        assertEquals(new GamePoint(-1, 0), new GamePoint(-3, 0).normalized());
        assertEquals(new GamePoint(-1, 1), new GamePoint(-3, 2).normalized());
    }

    @Test
    public void testTransleted(){
        val point = new GamePoint(0, 0);
        assertEquals(new GamePoint(1, 0), point.translated(Direction.RIGHT));
        assertEquals(new GamePoint(-1, 0), point.translated(Direction.LEFT));
        assertEquals(new GamePoint(0, -1), point.translated(Direction.UP));
        assertEquals(new GamePoint(0, 1), point.translated(Direction.DOWN));
    }
}
