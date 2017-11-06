package ru.leoltron.snake.game.entity;

import lombok.Getter;

public abstract class FieldObject {

    @Getter
    private boolean isDead;

    public abstract void onCollisionWith(FieldObject object);

    @SuppressWarnings("WeakerAccess")
    public void setDead() {
        isDead = true;
    }

    public void tick() {
    }

    public abstract FieldObject clone();
}
