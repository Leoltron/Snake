package ru.leoltron.snake.game;

import ru.leoltron.snake.game.entity.FieldObject;

@SuppressWarnings("WeakerAccess")
public class CollisionException extends Exception {
    public final FieldObject object1;
    public final FieldObject object2;

    public CollisionException(FieldObject object1, FieldObject object2) {
        super(String.format("Unresolved collision between %s and %s", object1, object2));
        this.object1 = object1;
        this.object2 = object2;
    }
}
