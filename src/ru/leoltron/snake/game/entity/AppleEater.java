package ru.leoltron.snake.game.entity;

import ru.leoltron.snake.game.GameField;
import ru.leoltron.snake.util.GamePoint;

public class AppleEater extends LivingFieldObject implements Edible {

    public static final int DEFAULT_APPLES_REQUIRED_TO_CLONE = 4;
    public static final int VIEW_DISTANCE = 4;

    private int applesRequiredToClone;
    private int applesEaten;

    public AppleEater() {
        this(DEFAULT_APPLES_REQUIRED_TO_CLONE, 0);
    }

    public AppleEater(int applesRequiredToClone, int applesEaten) {
        this.applesRequiredToClone = applesRequiredToClone;
        this.applesEaten = applesEaten;
    }

    @Override
    public void tick(GameField field, GamePoint location) {
        //TODO: move itself towards the apple and away from snake head (SnakePart.isHead())
        //TODO: if ate enough apples and there's a place for child, reset applesEaten and clone itself
    }

    @Override
    public void onCollisionWith(FieldObject object) {
        if (object instanceof Apple) {
            object.setDead();
            this.applesEaten++;
        } else
            this.setDead();
    }

    @Override
    public FieldObject clone() {
        return new AppleEater(applesRequiredToClone, applesEaten);
    }

    @Override
    public int getFoodValue() {
        return applesEaten;
    }
}
