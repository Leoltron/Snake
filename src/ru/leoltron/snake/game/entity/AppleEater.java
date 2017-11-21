package ru.leoltron.snake.game.entity;

import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.GameField;
import ru.leoltron.snake.util.GamePoint;
import ru.leoltron.snake.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

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
        Direction direction = getOptimalDirection(field, location,
                field.getNeighborhood(location, VIEW_DISTANCE, false));
        if (direction == null)
            return;
        field.removeEntityAt(location);
        location = location.translated(direction);
        field.addEntity(location, this);
        if (applesEaten < applesRequiredToClone)
            return;
        ArrayList<Direction> directions = new ArrayList<>();
        for (val nextDirection: Direction.values()){
            val nextLocation = location.translated(nextDirection);
            if (field.isFree(nextLocation))
                directions.add(nextDirection);
        }
        if (directions.isEmpty())
            return;
        applesEaten = 0;
        val nextLocation = location.translated(directions.get(new Random().nextInt(directions.size())));
        field.addEntity(nextLocation, clone());
    }

    private Direction getOptimalDirection(GameField field, GamePoint location,
                                         Collection<Pair<GamePoint, FieldObject>> nearestObjects){
        double optimalCost = -5;
        ArrayList<Direction> answer = new ArrayList<>();
        for (val direction: Direction.values()){
            double currentCost = getCostOfPoint(field, location.translated(direction), nearestObjects);
            if (currentCost > optimalCost){
                optimalCost = currentCost;
                answer.clear();
            }
            if (currentCost == optimalCost)
                answer.add(direction);
        }
        return answer.get(new Random().nextInt(answer.size()));
    }

    private double getCostOfPoint(GameField field, GamePoint location,
                                  Collection<Pair<GamePoint, FieldObject>> nearestObjects){
        double currentCost = 0;
        val fieldObject = field.getObjectAt(location);
        if (fieldObject != null && fieldObject.getClass() == Wall.class)
            return -1000;
        for (val object : nearestObjects){
            if (object.getItem2().getClass() == Apple.class)
                currentCost += 4 - location.euclideanDistanceTo(object.getItem1());
            else if (object.getItem2().getClass() == SnakePart.class){
                val sneakPart = (SnakePart)object.getItem2();
                if (sneakPart.isHead())
                    currentCost += location.euclideanDistanceTo(object.getItem1()) - 5;
            }
        }
        return currentCost;
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
