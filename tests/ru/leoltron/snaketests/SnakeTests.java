package ru.leoltron.snaketests;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.Game;
import ru.leoltron.snake.game.GameField;
import ru.leoltron.snake.game.controller.SingleLevelGameController;
import ru.leoltron.snake.game.controller.bonusGenerator.AppleGenerator;
import ru.leoltron.snake.game.controller.fieldGenerator.BorderGameFieldGenerator;
import ru.leoltron.snake.game.controller.snake.SnakeController;
import ru.leoltron.snake.game.entity.SnakePart;
import ru.leoltron.snake.util.GamePoint;

public class SnakeTests extends Assert {

    private class EmptyAppleGenerator implements AppleGenerator {

        @Override
        public void onStartNewGame(GameField field) {
        }

        @Override
        public void tick(GameField field) {
        }
    }

    private static void twistSnake(Game game, SnakeController controller) {
        controller.setCurrentDirection(Direction.UP);
        game.tick();

        controller.setCurrentDirection(Direction.RIGHT);
        game.tick();

        controller.setCurrentDirection(Direction.DOWN);
        game.tick();

        controller.setCurrentDirection(Direction.LEFT);
        game.tick();
    }

    @Test
    public void testSnakeSuicide() {
        val snakeController = new SnakeController(5);
        val game = new Game(new SingleLevelGameController(
                new EmptyAppleGenerator(),
                new BorderGameFieldGenerator(),
                snakeController),
                10, 10);
        game.startNewGame();

        twistSnake(game, snakeController);

        assertTrue(game.isGameOver());
    }

    @Test
    public void testSnakeTailFollowing() {
        val snakeController = new SnakeController(4);
        val game = new Game(new SingleLevelGameController(
                new EmptyAppleGenerator(),
                new BorderGameFieldGenerator(),
                snakeController),
                10, 10);
        game.startNewGame();

        twistSnake(game, snakeController);

        assertFalse(game.isGameOver());
    }

    @Test
    public void testSnakeMoving() {
        val width = 10;
        val height = 10;

        val controller = new SnakeController(4);
        val game = new Game(new SingleLevelGameController(
                new EmptyAppleGenerator(),
                new BorderGameFieldGenerator(),
                controller),
                width, height);
        game.startNewGame();

        val headBeforeTickLocation = findSnakeHead(game, 0, 0, width, height);

        controller.setCurrentDirection(Direction.UP);
        game.tick();

        val headAfterTickLocation = findSnakeHead(game, 0, 0, width, height);

        assertEquals(new GamePoint(0, -1), headAfterTickLocation.subtract(headBeforeTickLocation));
    }

    private static GamePoint findSnakeHead(Game game, int startX, int startY, int endX, int endY) {
        for (int x = startX; x < endX; x++)
            for (int y = startY; y < endY; y++) {
                val fieldObj = game.getObjectAt(x, y);
                if (fieldObj != null && fieldObj instanceof SnakePart && ((SnakePart) fieldObj).isHead())
                    return new GamePoint(x, y);
            }
        return null;
    }
}
