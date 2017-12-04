package ru.leoltron.snake.game.controller.snake;

import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.entity.*;
import ru.leoltron.snake.game.field.GameField;
import ru.leoltron.snake.util.BijectionHashMap;
import ru.leoltron.snake.util.GamePoint;
import ru.leoltron.snake.util.Triplet;
import ru.leoltron.snake.util.algorithms.graph.SimpleEdge;
import ru.leoltron.snake.util.algorithms.graph.SimpleGraph;
import ru.leoltron.snake.util.algorithms.graph.Vertex;
import ru.leoltron.snake.util.algorithms.graph.WeightedVertex;

import java.util.HashMap;
import java.util.Map;

import static ru.leoltron.snake.util.algorithms.Algorithms.bfs;

public class SimpleAISnakeController extends AISnakeController {
    private HashMap<Class, Integer> costOfClass;

    public SimpleAISnakeController(int playerId) {
        this(playerId, DEFAULT_SNAKE_LENGTH);
    }

    public SimpleAISnakeController(int playerId, int snakeLength) {
        super(playerId, snakeLength);
        costOfClass = new HashMap<>();
        costOfClass.put(Apple.class, 20);
        costOfClass.put(AppleEater.class, 5);
        costOfClass.put(SnakePart.class, -20);
    }

    @Override
    public void preTick(GameField field) {
        val fieldInfo = BuildGraph(field);
        SimpleGraph graph = fieldInfo.getItem1();
        val currentGamePoint = findHead(field, getPlayerId());
        WeightedVertex head = fieldInfo.getItem2().get(currentGamePoint);
        if (head == null)
            return;
        WeightedVertex targetVertex = getTargetVartex(graph, head);
        if (targetVertex == null)
            return;
        WeightedVertex nextVertex = getNearestVertexToHead(graph, targetVertex, head);
        if (nextVertex == null)
            return;
        val nextGamePoint = fieldInfo.getItem3().get(nextVertex);
        setCurrentDirection(Direction.fromGamePoint(nextGamePoint.subtract(currentGamePoint)));
    }

    private WeightedVertex getTargetVartex(SimpleGraph graph, WeightedVertex head) {
        HashMap<Vertex, Integer> distances = null;
        try {
            distances = bfs(head);
        } catch (InterruptedException ignored) {
        }
        WeightedVertex answer = null;
        assert distances != null;
        for (val vertex : distances.keySet()) {
            val weightedVertex = (WeightedVertex) vertex;
            if (weightedVertex == head) continue;
            if (answer == null || Greater(weightedVertex, answer, distances))
                answer = weightedVertex;
        }
        return answer;
    }

    private WeightedVertex getNearestVertexToHead(SimpleGraph graph, WeightedVertex start, WeightedVertex head) {
        HashMap<Vertex, Integer> distances = null;
        try {
            distances = bfs(start);
        } catch (InterruptedException ignored) {
        }
        WeightedVertex nearestVertex = null;
        for (val edges : head.getHeighbors()) {
            val nextVertex = (WeightedVertex) edges.getTo();
            if (nextVertex.getWeight() < 0)
                continue;
            if (nearestVertex == null)
                nearestVertex = nextVertex;
            int distanceToNearest = distances.getOrDefault(nearestVertex, 1000);
            int distanceToNext = distances.getOrDefault(nextVertex, 1000);
            if (distanceToNearest > distanceToNext
                    || distanceToNearest == distanceToNext && nextVertex.getWeight() > nearestVertex.getWeight())
                nearestVertex = nextVertex;
        }
        return nearestVertex;
    }

    private boolean Greater(WeightedVertex a, WeightedVertex b, HashMap<Vertex, Integer> distances) {
        int distA = distances.get(a);
        int distB = distances.get(b);
        return (a.getWeight() * distB > b.getWeight() * distA);
    }

    private GamePoint findHead(GameField field, int playerId) {
        for (val object : field.getFieldObjects()) {
            if (object.getValue().getClass() != SnakePart.class) continue;
            val sneakPart = (SnakePart) object.getValue();
            if (sneakPart.isHead() && sneakPart.getSnakeOwnerId() == playerId)
                return object.getKey();
        }
        return null;
    }

    private Triplet<SimpleGraph, Map<GamePoint, WeightedVertex>, Map<WeightedVertex, GamePoint>>
    BuildGraph(GameField field) {
        SimpleGraph graph = new SimpleGraph();
        val gamePointAndVertex = new BijectionHashMap<GamePoint, WeightedVertex>();
        for (int i = 0; i < field.getFieldWidth(); i++)
            for (int j = 0; j < field.getFieldHeight(); j++) {
                FieldObject currentObject = field.getObjectAt(i, j);
                if (currentObject != null && currentObject.getClass() == Wall.class)
                    continue;
                int cost = 0;
                if (currentObject != null)
                    cost = costOfClass.getOrDefault(currentObject.getClass(), 0);
                val currentVertex = new WeightedVertex(cost);
                gamePointAndVertex.put(new GamePoint(i, j), currentVertex);
                graph.addVertex(currentVertex);
            }
        for (val vertex : graph.getVertices()) {
            val currentPoint = gamePointAndVertex.getReverse((WeightedVertex) vertex);
            for (val direction : Direction.values()) {
                val nextPoint = currentPoint.translated(direction);
                val nextObject = field.getObjectAt(nextPoint);
                if (nextObject != null && nextObject.getClass() == Wall.class)
                    continue;
                graph.addEdge(new SimpleEdge(vertex, gamePointAndVertex.get(nextPoint)));
            }
        }
        return Triplet.create(graph,
                gamePointAndVertex.toOneDirectionalMap(),
                gamePointAndVertex.toReversedOneDirectionalMap());
    }
}
