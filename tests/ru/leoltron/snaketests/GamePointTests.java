package ru.leoltron.snaketests;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;
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
}
