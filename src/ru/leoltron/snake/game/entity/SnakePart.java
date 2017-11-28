package ru.leoltron.snake.game.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.controller.snake.SnakeController;

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

    public SnakePart(SnakeController snakeController) {
        this.snakeController = snakeController;
    }

    public SnakePart(SnakeController snakeController, int snakeOwnerId) {
        this(snakeController);
        this.snakeOwnerId = snakeOwnerId;
    }

    public boolean isHead(){
        return nextPartDirection == null;
    }

    public boolean isTail(){
        return prevPartDirection == null;
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
        return part;
    }

    public boolean isSnakeDead() {
        return snakeController.isSnakeDeadSimple();
    }
}
