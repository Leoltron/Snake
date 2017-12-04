package ru.leoltron.snake.game.controller.snake;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import ru.leoltron.snake.game.CurrentDirectionHolder;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.entity.Edible;
import ru.leoltron.snake.game.entity.SnakePart;
import ru.leoltron.snake.game.field.GameField;
import ru.leoltron.snake.util.GamePoint;

import java.util.LinkedList;

public class SnakeController implements CurrentDirectionHolder {
    protected static final int DEFAULT_SNAKE_LENGTH = 4;
    private final int snakeLength;
    @SuppressWarnings("WeakerAccess")
    protected LinkedList<GamePoint> body;
    @Setter
    private GamePoint startPoint = null;
    @Setter
    private Direction startDirection = Direction.UP;
    @Getter
    private int playerId;
    @Getter
    @Setter
    private int applesEatenRecently;
    private int snakePartsGoingToAdd;
    private Direction currentDirection;
    private boolean isDead = false;

    public SnakeController() {
        this(0);
    }

    public SnakeController(int playerId) {
        this(playerId, DEFAULT_SNAKE_LENGTH);
    }

    public SnakeController(int playerId, int snakeLength) {
        if (snakeLength < 1)
            throw new IllegalArgumentException("Snake length must be positive!");
        this.playerId = playerId;
        this.snakeLength = snakeLength;
    }

    private void respawnSnake(GameField field, GamePoint startGamePoint, int initialLength) {
        clearFieldFromSnake(field);
        isDead = false;
        currentDirection = startDirection;

        body = new LinkedList<>();
        body.addFirst(startGamePoint);
        field.addEntity(startGamePoint, new SnakePart(this, playerId));
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
        return body == null ? 0 : body.size();
    }

    public void onFoodEaten(Edible edible) {
        snakePartsGoingToAdd += edible.getFoodValue();
        applesEatenRecently += edible.getFoodValue();
    }

    public void tick(GameField field) {
        if (isSnakeDead(field)) return;

        if (snakePartsGoingToAdd > 0)
            snakePartsGoingToAdd--;
        else
            shortenTail(field);

        val headLoc = getHeadLocation();
        final SnakePart snakePart = (SnakePart) field.getObjectAt(headLoc);
        snakePart.setNextPartDirection(currentDirection);
        field.removeEntityAt(headLoc);
        field.addEntity(headLoc, snakePart);

        addNewHead(field);
    }

    private void addNewHead(GameField field) {
        val newHead = new SnakePart(this, playerId);
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

    @Override
    public Direction getCurrentDirection() {
        return currentDirection;
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
                field.getObjectAt(getHeadLocation()).isDead() ||
                isDead;
    }

    public boolean isSnakeDeadSimple() {
        return body == null || body.isEmpty() || isDead;
    }

    public void setSnakeDead() {
        isDead = true;
    }
}
