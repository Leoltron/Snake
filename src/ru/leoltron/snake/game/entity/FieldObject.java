package ru.leoltron.snake.game.entity;

import lombok.Getter;
import lombok.NonNull;

public abstract class FieldObject {

    @Getter
    private boolean isDead;

    public abstract void onCollisionWith(FieldObject object);

    @SuppressWarnings("WeakerAccess")
    public void setDead() {
        isDead = true;
    }

    public abstract FieldObject clone();

    public static boolean equals(FieldObject f1, FieldObject f2) {
        if (f1 == null && f2 == null) return true;
        if (f1 == null || f2 == null) return false;
        return f1.equals(f2);
    }

    public abstract boolean equals(@NonNull FieldObject other);

    public abstract FieldObject deserializeFromString(String s);

    public abstract String serializeToString();
}
