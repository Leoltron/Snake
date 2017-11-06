package ru.leoltron.snake.game.controller.module;

import lombok.Setter;
import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.GameField;
import ru.leoltron.snake.game.entity.SnakePart;
import ru.leoltron.snake.util.GamePoint;

import java.util.LinkedList;

public class SnakeController {
    private static final int DEFAULT_SNAKE_LENGTH = 4;
    private final int snakeLength;

    @Setter
    private GamePoint startPoint = null;
    @Setter
    private Direction startDirection = Direction.UP;

    @SuppressWarnings("WeakerAccess")
    protected LinkedList<GamePoint> body;
    private int snakePartsGoingToAdd;
    private Direction currentDirection;

    public SnakeController() {
        this(DEFAULT_SNAKE_LENGTH);
    }

    public SnakeController(int snakeLength) {
        if (snakeLength < 1)
            throw new IllegalArgumentException("Snake length must be positive!");
        this.snakeLength = snakeLength;
    }

    private void respawnSnake(GameField field, GamePoint startGamePoint, int initialLength) {
        clearFieldFromSnake(field);

        currentDirection = startDirection;

        body = new LinkedList<>();
        body.addFirst(startGamePoint);
        field.addEntity(startGamePoint, new SnakePart(this));
        snakePartsGoingToAdd = initialLength - 1;

    }

    private void clearFieldFromSnake(GameField field) {
        if (body != null)
            for (val point : body) {
                val object = field.getObjectAt(point);
                if (object != null && object instanceof SnakePart)
                    field.removeEntityAt(point);
            }
    }

    private void shortenTail(GameField field) {
        field.removeEntityAt(body.removeLast());
        if (!body.isEmpty())
            ((SnakePart) field.getObjectAt(getTailLocation())).setPrevPartDirection(null);
    }

    private GamePoint getTailLocation() {
        return body.getLast();
    }

    private int getSnakeSize() {
        return body.size();
    }

    public void onAppleEaten() {
        snakePartsGoingToAdd++;
    }

    public void tick(GameField field) {
        if (isSnakeDead(field)) return;

        if (snakePartsGoingToAdd > 0)
            snakePartsGoingToAdd--;
        else
            shortenTail(field);

        val headLoc = getHeadLocation();
        ((SnakePart) field.getObjectAt(headLoc)).setNextPartDirection(currentDirection);

        addNewHead(field);
    }

    private void addNewHead(GameField field) {
        val newHead = new SnakePart(this);
        newHead.setPrevPartDirection(currentDirection.reversed());

        val location = getHeadLocation().translated(currentDirection);

        body.addFirst(location);
        field.addEntity(location, newHead);
    }

    public void respawnSnake(GameField gameField) {
        respawnSnake(
                gameField,
                startPoint != null ? startPoint : new GamePoint(3, gameField.getFieldHeight() - 3),
                snakeLength);
    }

    public void setCurrentDirection(Direction direction) {
        if (getSnakeSize() > 1 && body.get(1).equals(getHeadLocation().translated(direction)))
            return;
        currentDirection = direction;
    }

    private GamePoint getHeadLocation() {
        return body.getFirst();
    }

    public boolean isSnakeDead(GameField field) {
        return body == null ||
                body.isEmpty() ||
                !(field.getObjectAt(getHeadLocation()) instanceof SnakePart) ||
                field.getObjectAt(getHeadLocation()).isDead();
    }
}
