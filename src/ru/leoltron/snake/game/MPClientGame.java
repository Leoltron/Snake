package ru.leoltron.snake.game;

import lombok.val;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.game.field.GameField;

import java.util.regex.Pattern;

public class MPClientGame implements GameInfo, CurrentDirectionHolder {
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("([\\d]+):([\\d]+):([\\w\\.]+):([^:]*)");
    private GameField field;
    private int time;
    private Direction currentDirection;

    public MPClientGame(int fieldWidth, int fieldHeight) {
        field = new GameField(fieldWidth, fieldHeight);
        time = 0;
    }

    public void updateField(int newTime, String[] changedObjectsDescriptions) {
        for (val description : changedObjectsDescriptions) {
            val matcher = DESCRIPTION_PATTERN.matcher(description);
            if (!matcher.matches()) {
                System.err.println("Invalid format of object change description line: " + description);
                continue;
            }
            val x = Integer.parseInt(matcher.group(1));
            val y = Integer.parseInt(matcher.group(2));
            val className = matcher.group(3);
            val fieldObjArgs = matcher.group(4);
            updateFieldAt(x, y, className, fieldObjArgs);
        }
        time = newTime;
    }

    private void updateFieldAt(int x, int y, String className, String fieldObjArgs) {
        field.removeEntityAt(x, y);
        if (!className.equals("null")) {
            try {
                val fieldObject = (FieldObject) Class.forName(className)
                        .getMethod("deserialize", String.class)
                        .invoke(null, fieldObjArgs);
                field.addEntity(x, y, fieldObject);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public int getTempPauseTime() {
        return 0;
    }

    @Override
    public int getTime() {
        return time;
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public FieldObject getObjectAt(int x, int y) {
        return field.getObjectAt(x, y);
    }

    @Override
    public int getFieldWidth() {
        return field.getFieldWidth();
    }

    @Override
    public int getFieldHeight() {
        return field.getFieldHeight();
    }

    @Override
    public Direction getCurrentDirection() {
        return currentDirection;
    }

    @Override
    public void setCurrentDirection(Direction direction) {
        currentDirection = direction;
    }
}
