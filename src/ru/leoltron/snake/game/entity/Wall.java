package ru.leoltron.snake.game.entity;

public class Wall extends FieldObject {

    public Wall() {
    }

    @Override
    public void onCollisionWith(FieldObject object) {
        object.setDead();
    }

    @Override
    public FieldObject clone() {
        return new Wall();
    }
}
