package ru.leoltron.snaketests;

import lombok.val;
import org.junit.Test;
import ru.leoltron.snake.util.MapUtils;

import java.util.HashMap;

import static org.junit.Assert.*;

public class MapUtilsTest {
    @Test
    public void testFillMap(){
        val result = new HashMap<Integer, Double>();
        new MapUtils(Integer.class, Double.class).fillMap(result, 1, 2., 3, 4., 5);
        val expected = new HashMap<Integer, Double>();
        expected.put(1, 2.0);
        expected.put(3, 4.0);
        assertEquals(expected, result);
    }

    @Test
    public void testTypes(){
        val result = new HashMap<Integer, Double>();
        try {
            new MapUtils(Integer.class, Double.class).fillMap(result, 1, 2, "str", 4, 5);
        } catch (Throwable a){
            return;
        }
        fail();
    }
}