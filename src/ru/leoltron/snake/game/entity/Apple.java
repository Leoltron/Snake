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

    public static FieldObject deserialize(String s) {
        return new Apple();
    }

    @Override
    public boolean equals(FieldObject other) {
        return other instanceof Apple;
    }

    @Override
    public FieldObject deserializeFromString(String s) {
        return deserialize(s);
    }

    @Override
    public String serializeToString() {
        return "";
    }

    @Override
    public int getFoodValue() {
        return 1;
    }
}
