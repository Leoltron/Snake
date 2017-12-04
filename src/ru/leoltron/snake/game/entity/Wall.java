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

    public static FieldObject deserialize(String s) {
        return new Wall();
    }

    @Override
    public boolean equals(FieldObject other) {
        return other instanceof Wall;
    }

    @Override
    public FieldObject deserializeFromString(String s) {
        return deserialize(s);
    }

    @Override
    public String serializeToString() {
        return "";
    }
}
