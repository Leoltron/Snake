package ru.leoltron.snake.game.entity;

import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.field.GameField;
import ru.leoltron.snake.util.GamePoint;
import ru.leoltron.snake.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

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

    private static List<Direction> getFreeNeighbours(GameField field, GamePoint location) {
        ArrayList<Direction> directionsAvailable = new ArrayList<>();
        for (val nextDirection : Direction.values()) {
            val nextLocation = location.translated(nextDirection);
            if (field.isFree(nextLocation))
                directionsAvailable.add(nextDirection);
        }
        return directionsAvailable;
    }

    @Override
    public void tick(GameField field, GamePoint curLocation) {
        Direction direction = getOptimalDirection(field, curLocation,
                field.getNeighborhood(curLocation, VIEW_DISTANCE, false));
        if (direction == null)
            return;
        curLocation = moveBy(field, curLocation, direction);

        if (applesEaten >= applesRequiredToClone)
            makeClone(field, curLocation);

    }

    private GamePoint moveBy(GameField field, GamePoint location, Direction direction) {
        field.removeEntityAt(location);
        location = location.translated(direction);
        field.addEntity(location, this);
        return location;
    }

    private void makeClone(GameField field, GamePoint location) {
        val directionsAvailable = getFreeNeighbours(field, location);
        if (directionsAvailable.isEmpty())
            return;
        applesEaten = 0;
        val nextLocation = location.translated(directionsAvailable.get(field.rand.nextInt(directionsAvailable.size())));
        field.addEntity(nextLocation, clone());
    }

    private static final Pattern SERIALIZATION_PATTERN = Pattern.compile("([\\d]+):([\\d]+)");

    public static FieldObject deserialize(String s) {
        val matcher = SERIALIZATION_PATTERN.matcher(s);
        if (!matcher.matches()) {
            System.err.println(AppleEater.class.toString() + ": Failed to deserialize string \"" + s + "\"");
            return null;
        }
        val appleEater = new AppleEater();
        appleEater.applesEaten = Integer.parseInt(matcher.group(1));
        appleEater.applesRequiredToClone = Integer.parseInt(matcher.group(2));
        return appleEater;
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

    private Direction getOptimalDirection(GameField field, GamePoint location,
                                          Collection<Pair<GamePoint, FieldObject>> nearestObjects) {
        double optimalCost = -5;
        List<Direction> answer = new ArrayList<>();
        for (val direction : Direction.values()) {
            double currentCost = getCostOfPoint(field, location.translated(direction), nearestObjects);
            if (currentCost > optimalCost) {
                optimalCost = currentCost;
                answer.clear();
            }
            if (currentCost == optimalCost)
                answer.add(direction);
        }
        if (answer.isEmpty())
            return null;
        return answer.get(field.rand.nextInt(answer.size()));
    }

    private double getCostOfPoint(GameField field, GamePoint location,
                                  Collection<Pair<GamePoint, FieldObject>> nearestObjects) {
        double currentCost = 0;
        val fieldObject = field.getObjectAt(location);
        if (fieldObject != null && fieldObject.getClass() != Apple.class)
            return -Double.MAX_VALUE;
        for (val object : nearestObjects) {
            if (object.getItem2().getClass() == Apple.class)
                currentCost += 4 - location.manhattanDistanceTo(object.getItem1());
            else if (object.getItem2().getClass() == SnakePart.class) {
                val sneakPart = (SnakePart) object.getItem2();
                if (sneakPart.isHead())
                    currentCost += location.manhattanDistanceTo(object.getItem1()) - 5;
            }
        }
        return currentCost;
    }

    @Override
    public boolean equals(FieldObject other) {
        if (other instanceof AppleEater) {
            val appleEater = (AppleEater) other;
            return appleEater.applesEaten == applesEaten && appleEater.applesRequiredToClone == applesRequiredToClone;
        }
        return false;
    }

    @Override
    public FieldObject deserializeFromString(String s) {
        return deserialize(s);
    }

    @Override
    public String serializeToString() {
        return String.format("%d:%d", applesEaten, applesRequiredToClone);
    }

    @Override
    public int getFoodValue() {
        return applesEaten + 1;
    }
}
