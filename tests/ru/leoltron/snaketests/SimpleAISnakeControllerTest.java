package ru.leoltron.snaketests;

import com.sun.xml.internal.fastinfoset.tools.FI_DOM_Or_XML_DOM_SAX_SAXEvent;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.controller.snake.SimpleAISnakeController;
import ru.leoltron.snake.game.entity.Apple;
import ru.leoltron.snake.game.entity.AppleEater;
import ru.leoltron.snake.game.entity.SnakePart;
import ru.leoltron.snake.game.entity.Wall;
import ru.leoltron.snake.game.field.GameField;
import ru.leoltron.snake.util.GamePoint;

import static org.junit.Assert.*;

public class SimpleAISnakeControllerTest {
    private GameField field;
    private SimpleAISnakeController controller;
    @Before
    public void init(){
        field = new GameField(5, 5);
        controller = new SimpleAISnakeController(1);
        field.addEntity(0, 0, new Wall());
        field.addEntity(0, 1, new Wall());
        field.addEntity(0, 2, new Wall());
        field.addEntity(0, 3, new Wall());
        field.addEntity(0, 4, new Wall());
        field.addEntity(1, 0, new Wall());
        field.addEntity(2, 0, new Wall());
        field.addEntity(3, 0, new Wall());
        field.addEntity(4, 0, new Wall());
        field.addEntity(4, 1, new Wall());
        field.addEntity(4, 2, new Wall());
        field.addEntity(4, 3, new Wall());
        field.addEntity(4, 4, new Wall());
        field.addEntity(1, 4, new Wall());
        field.addEntity(2, 4, new Wall());
        field.addEntity(3, 4, new Wall());
        controller.respawnSnake(field);
    }

    @Test
    public void preTick() throws Exception {
        assertTrue(field.getObjectAt(3, 2) instanceof SnakePart);
        field.addEntity(3, 3, new Apple());
        field.addEntity(3, 1, new AppleEater());
        controller.preTick(field);
        assertEquals(Direction.DOWN, controller.getCurrentDirection());
    }

}