package ru.leoltron.snake.game.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.controller.snake.SnakeController;

import java.util.regex.Pattern;

public class SnakePart extends FieldObject {

    private SnakeController snakeController;

    @Getter
    @Setter
    private int snakeOwnerId;
    @Getter
    @Setter
    private Direction prevPartDirection;
    @Getter
    @Setter
    private Direction nextPartDirection;

    private static final Pattern SERIALIZATION_PATTERN = Pattern.compile("([\\d]+):([\\w]+):([\\w]+)");

    public SnakePart(SnakeController snakeController) {
        this.snakeController = snakeController;
    }

    public SnakePart(SnakeController snakeController, int snakeOwnerId) {
        this(snakeController);
        this.snakeOwnerId = snakeOwnerId;
    }

    public SnakePart() {
        this(null);
    }

    public static FieldObject deserialize(String s) {
        val matcher = SERIALIZATION_PATTERN.matcher(s);
        if (!matcher.matches()) {
            System.err.println(SnakePart.class.toString() + ": Failed to deserialize string \"" + s + "\"");
            return null;
        }
        val snakePart = new SnakePart(null);
        snakePart.snakeOwnerId = Integer.parseInt(matcher.group(1));
        snakePart.prevPartDirection = matcher.group(2).equals("null") ? null : Direction.valueOf(matcher.group(2));
        snakePart.nextPartDirection = matcher.group(3).equals("null") ? null : Direction.valueOf(matcher.group(3));
        return snakePart;
    }

    @Override
    public void onCollisionWith(FieldObject object) {
        if (object instanceof Edible)
            snakeController.onFoodEaten((Edible) object);
        else if (this.isHead() && !(object instanceof SnakePart && ((SnakePart) object).isSnakeDead())) {
            System.out.println("Snake died from collision with " + object.getClass().getName());
            setDead();
            snakeController.setSnakeDead();
        } else if (!this.isHead() && this.isSnakeDead())
            setDead();
    }

    @Override
    public FieldObject clone() {
        val part = new SnakePart(snakeController);
        part.prevPartDirection = prevPartDirection;
        part.nextPartDirection = nextPartDirection;
        part.snakeOwnerId = snakeOwnerId;
        return part;
    }

    public boolean isHead() {
        return nextPartDirection == null;
    }

    public boolean isTail() {
        return prevPartDirection == null;
    }

    @Override
    public boolean equals(FieldObject other) {
        if (other instanceof SnakePart) {
            val snakePart = (SnakePart) other;
            return snakePart.snakeOwnerId == snakeOwnerId &&
                    snakePart.prevPartDirection == prevPartDirection &&
                    snakePart.nextPartDirection == nextPartDirection;
        }
        return false;
    }

    @Override
    public FieldObject deserializeFromString(String s) {
        return deserialize(s);
    }

    @Override
    public String serializeToString() {
        return String.format("%d:%s:%s", snakeOwnerId,
                prevPartDirection == null ? "null" : prevPartDirection.name(),
                nextPartDirection == null ? "null" : nextPartDirection.name());
    }

    public boolean isSnakeDead() {
        return snakeController.isSnakeDeadSimple();
    }
}
