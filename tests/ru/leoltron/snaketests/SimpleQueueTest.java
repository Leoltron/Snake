package ru.leoltron.snaketests;

import org.junit.Before;
import org.junit.Test;
import ru.leoltron.snake.util.SimpleQueue;

import static org.junit.Assert.*;

public class SimpleQueueTest {
    private SimpleQueue<Integer> queue;

    @Before
    public void init(){
        queue = new SimpleQueue<>();
    }

    @Test
    public void isEmpty(){
        assertTrue(queue.isEmpty());
    }

    @Test
    public void enqueue(){
        queue.enqueue(1);
        queue.enqueue(2);
        assertEquals(2, queue.getSize());
    }

    @Test
    public void dequeue(){
        queue.enqueue(1);
        queue.enqueue(2);
        assertEquals(1, (int)queue.dequeue());
        assertEquals(1, queue.getSize());
        assertEquals(2, (int)queue.dequeue());
        try{
            queue.dequeue();
        } catch (NullPointerException a){
            return;
        }
        fail();
    }
}