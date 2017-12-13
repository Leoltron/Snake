package ru.leoltron.snaketests;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import ru.leoltron.snake.util.BijectionHashMap;

import java.util.HashMap;

import static org.junit.Assert.*;

public class BijectionHashMapTest {
    private BijectionHashMap<Integer, Double> map;

    @Before
    public void init(){
        map = new BijectionHashMap<>();
        map.put(1, 2.0);
        map.put(2, 3.0);
    }

    @Test
    public void put() throws Exception {
    }

    @Test
    public void get() throws Exception {
        assertEquals( 2.0, map.get(1), 0);
        assertEquals( 3.0, map.get(2), 0);
    }

    @Test
    public void getReverse() throws Exception {
        assertEquals(1, (int)map.getReverse(2.0));
        assertEquals(2, (int)map.getReverse(3.0));
    }

    @Test
    public void toOneDirectionalMap() throws Exception {
        val expected = new HashMap<Integer, Double>();
        expected.put(1, 2.0);
        expected.put(2, 3.0);
        assertEquals(map.toOneDirectionalMap(), expected);
    }

    @Test
    public void toReversedOneDirectionalMap() throws Exception {
        val expected = new HashMap<Double, Integer>();
        expected.put(2.0, 1);
        expected.put(3.0, 2);
        assertEquals(map.toReversedOneDirectionalMap(), expected);
    }

}