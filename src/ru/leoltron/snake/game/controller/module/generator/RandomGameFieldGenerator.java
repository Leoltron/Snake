package ru.leoltron.snake.game.controller.module.generator;


import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.GameField;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.util.GamePoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomGameFieldGenerator extends BorderGameFieldGenerator {

    private final Random random = new Random();

    public void generateFieldObjects(GameField gameField, int length, int part){
        gameField.clear();
        for (val entry :
                generateFieldObjects(gameField.getFieldWidth(), gameField.getFieldHeight(), length, part).entrySet())
            gameField.addEntity(entry.getKey(), entry.getValue());
    }

    @Override
    public Map<GamePoint, FieldObject> generateFieldObjects(int fieldWidth, int fieldHeight) {
            return generateFieldObjects(fieldWidth, fieldHeight, 6, 10);
    }

    protected Map<GamePoint, FieldObject> generateFieldObjects(int fieldWidth, int fieldHeight, int length, int part){
        boolean[][] isWall;
        val objects = new HashMap<GamePoint, FieldObject>();
        do{
            isWall = generateRandomMap(fieldWidth, fieldHeight, length, part);
        } while (!canBeMap(isWall));
        for (int i = 1; i < fieldWidth - 1; i++) {
            for (int j = 1; j < fieldHeight - 1; j++) {
                if (isWall[i][j])
                    addWallAt(objects, i, j);
            }
        }
        objects.putAll(new BorderGameFieldGenerator().generateFieldObjects(fieldWidth, fieldHeight));
        return objects;
    }

    private boolean canBeMap(boolean[][] isWall) {
        return true;
    }

    private boolean[][] generateRandomMap(int width, int height, int length, double part){
        boolean[][] isWall = new boolean[width][height];
        part = Math.min(part, 10);
        length = Math.min(length, Math.min(height / 5, width / 5));
        int countOfWalls = (int)((width - 1) * (height - 1) * part / 100);
        while (countOfWalls > 0){
            int currentLength = random.nextInt(length + 1);
            generatePartOfMap(isWall, currentLength);
            countOfWalls -= currentLength;
            length = Math.min(length, countOfWalls);
        }
        return isWall;
    }

    private void generatePartOfMap(boolean[][] isWall, int currentLength) {
        int width = isWall.length;
        int height = isWall[0].length;
        GamePoint position = generateRandomPosition(width, height);
        for (int i = 0; i < currentLength; i++) {
            isWall[position.x][position.y] = true;
            position = getRandomNextPosition(position, width, height);
        }
    }

    private boolean inRange(GamePoint position, int width, int height){
        return 0 < position.x && position.x < width - 1 &&
                0 < position.y && position.y < height - 1;
    }

    private GamePoint getRandomNextPosition(GamePoint position, int width, int height){
        GamePoint nextPosition;
        do {
            nextPosition = position.translated(Direction.values()[random.nextInt(4)]);
        } while (!inRange(nextPosition, width, height));
        return nextPosition;
    }

    private GamePoint generateRandomPosition(int width, int height){
        return new GamePoint(random.nextInt(width), random.nextInt(height));
    }
}
