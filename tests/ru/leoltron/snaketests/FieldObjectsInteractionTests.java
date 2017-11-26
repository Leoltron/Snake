package ru.leoltron.snaketests;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import ru.leoltron.snake.game.GameField;
import ru.leoltron.snake.game.controller.snake.SnakeController;
import ru.leoltron.snake.game.entity.Apple;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.game.entity.SnakePart;
import ru.leoltron.snake.game.entity.Wall;

public class FieldObjectsInteractionTests extends Assert {


    private static void collide(FieldObject fieldObject1, FieldObject fieldObject2) {
        val field = new GameField(10, 10);
        field.addEntity(5, 5, fieldObject1);
        field.addEntity(5, 5, fieldObject2);
    }

    @Test
    public void testSnakeWallBreak() {
        val snake = new SnakePart(new SnakeController());
        val wall = new Wall();

        collide(snake, wall);

        assertTrue(snake.isDead());
        assertFalse(wall.isDead());
    }

    @Test
    public void testSnakeAppleEat() {
        val snake = new SnakePart(new SnakeController());
        val apple = new Apple();

        collide(snake, apple);

        assertTrue(apple.isDead());
        assertFalse(snake.isDead());
    }
}
