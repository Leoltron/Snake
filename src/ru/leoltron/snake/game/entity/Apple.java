package ru.leoltron.snake.game.entity;

public class Apple extends FieldObject implements Edible {

    @Override
    public void onCollisionWith(FieldObject object) {
        setDead();
    }

    @Override
    public FieldObject clone() {
        return new Apple();
    }

    @Override
    public int getFoodValue() {
        return 1;
    }
}
