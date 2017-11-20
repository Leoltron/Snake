package ru.leoltron.snake.game.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.controller.module.SnakeController;

public class SnakePart extends FieldObject {

    private SnakeController snakeController;

    @Getter
    @Setter
    private Direction prevPartDirection;
    @Getter
    @Setter
    private Direction nextPartDirection;

    public SnakePart(SnakeController snakeController) {
        this.snakeController = snakeController;
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
        else
            setDead();
    }

    @Override
    public FieldObject clone() {
        val part = new SnakePart(this.snakeController);
        part.prevPartDirection = prevPartDirection;
        part.nextPartDirection = nextPartDirection;
        return part;
    }
}
