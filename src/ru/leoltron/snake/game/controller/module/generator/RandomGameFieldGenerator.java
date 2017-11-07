package ru.leoltron.snake.game.controller.module.generator;


import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.util.GamePoint;
import ru.leoltron.snake.util.algorithms.Algorithms;
import ru.leoltron.snake.util.algorithms.graph.SimpleEdge;
import ru.leoltron.snake.util.algorithms.graph.SimpleGraph;
import ru.leoltron.snake.util.algorithms.graph.Vertex;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomGameFieldGenerator extends BorderGameFieldGenerator {

    private final Random random = new Random();
    private int length;
    private int part;

    public RandomGameFieldGenerator() {
        this(8, 15);
    }

    private RandomGameFieldGenerator(int length, int part) {
        this.length = length;
        this.part = setInBounds(part, 0, 15);
    }

    private static int setInBounds(int value, int min, int max) {
        if (min > max)
            throw new IllegalArgumentException(String.format("max (%d) must be more or equal than min (%d)!", max, min));
        return Math.max(Math.min(value, max), min);
    }

    @Override
    public Map<GamePoint, FieldObject> generateFieldObjects(int fieldWidth, int fieldHeight) {
        return generateFieldObjects(fieldWidth, fieldHeight, this.length, this.part);
    }

    private Map<GamePoint, FieldObject> generateFieldObjects(int fieldWidth, int fieldHeight, int length, int part) {
        boolean[][] isWall;
        val objects = new HashMap<GamePoint, FieldObject>();
        do {
            isWall = generateRandomMap(fieldWidth, fieldHeight, length, part);
        } while (!canBeMap(isWall));
        for (int i = 0; i < fieldWidth; i++) {
            for (int j = 0; j < fieldHeight; j++) {
                if (isWall[i][j])
                    addWallAt(objects, i, j);
            }
        }
        return objects;
    }

    private boolean canBeMap(boolean[][] isWall) {
        val graph = buildGraph(isWall);
        return Algorithms.getBridges(graph).isEmpty()
                && Algorithms.buildConnectedComponents(graph).size() == 1;
    }

    private SimpleGraph buildGraph(boolean[][] isWall) {
        SimpleGraph graph = new SimpleGraph();
        val vertexFromGamePoint = new HashMap<GamePoint, Vertex>();
        int width = isWall.length;
        int height = isWall[0].length;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (isWall[i][j]) continue;
                val current = new GamePoint(i, j);
                val currentVertex = new Vertex();
                vertexFromGamePoint.put(current, currentVertex);
                graph.getVertices().add(currentVertex);
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (isWall[i][j]) continue;
                val currentPosition = new GamePoint(i, j);
                val currentVertex = vertexFromGamePoint.get(currentPosition);
                for (val direction : Direction.values()) {
                    val nextPosition = currentPosition.translated(direction);
                    if (inRange(nextPosition, width, height) && !isWall[nextPosition.x][nextPosition.y]) {
                        val nextVertex = vertexFromGamePoint.get(nextPosition);
                        graph.addEdge(new SimpleEdge(currentVertex, nextVertex));
                    }
                }
            }
        }
        return graph;
    }

    private boolean[][] generateRandomMap(int width, int height, int length, double part) {
        boolean[][] isWall = new boolean[width][height];
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                isWall[i][j] = false;
        for (int i = 0; i < width; i++) {
            isWall[i][0] = true;
            isWall[i][height - 1] = true;
        }
        for (int i = 0; i < height; i++) {
            isWall[0][i] = true;
            isWall[width - 1][i] = true;
        }
        length = Math.min(length, Math.min(height / 4, width / 4));
        int countOfWalls = (int) ((width - 1) * (height - 1) * part / 100);
        while (countOfWalls > 0) {
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
        Direction direction = Direction.values()[random.nextInt(4)];
        GamePoint position = generateRandomPosition(width, height);
        for (int i = 0; i < currentLength && inRange(position, width, height); i++) {
            isWall[position.x][position.y] = true;
            position = position.translated(direction);
        }
    }

    private boolean inRange(GamePoint position, int width, int height) {
        return 0 < position.x && position.x < width - 1 &&
                0 < position.y && position.y < height - 1;
    }

    private GamePoint generateRandomPosition(int width, int height) {
        return new GamePoint(random.nextInt(width), random.nextInt(height));
    }
}
