package ru.leoltron.snake.util.algorithms;

import lombok.val;
import ru.leoltron.snake.game.Direction;
import ru.leoltron.snake.game.Game;
import ru.leoltron.snake.game.entity.FieldObject;
import ru.leoltron.snake.game.entity.Wall;
import ru.leoltron.snake.game.field.GameField;
import ru.leoltron.snake.util.BijectionHashMap;
import ru.leoltron.snake.util.GamePoint;
import ru.leoltron.snake.util.algorithms.graph.SimpleEdge;
import ru.leoltron.snake.util.algorithms.graph.SimpleGraph;
import ru.leoltron.snake.util.algorithms.graph.Vertex;

public class GameFieldUtil {

    public static <T extends Vertex> void tryAddEdges(Vertex vertex,
                                                       BijectionHashMap<GamePoint, T> gamePointAndVertex,
                                                       SimpleGraph graph,
                                                       GameField field){
        val currentPoint = gamePointAndVertex.getReverse((T)vertex);
        for (val direction : Direction.values()) {
            val nextPoint = currentPoint.translated(direction);
            val nextObject = field.getObjectAt(nextPoint);
            if (nextObject != null && nextObject.getClass() == Wall.class)
                continue;
            graph.addEdge(new SimpleEdge(vertex, gamePointAndVertex.get(nextPoint)));
        }
    }

    public static SimpleGraph buildGraph(GameField field){
        SimpleGraph graph = new SimpleGraph();
        val gamePointAndVertex = new BijectionHashMap<GamePoint, Vertex>();
        for (int i = 0; i < field.getFieldWidth(); i++)
            for (int j = 0; j < field.getFieldHeight(); j++) {
                FieldObject currentObject = field.getObjectAt(i, j);
                if (currentObject != null && currentObject.getClass() == Wall.class)
                    continue;
                val currentVertex = new Vertex();
                gamePointAndVertex.put(new GamePoint(i, j), currentVertex);
                graph.addVertex(currentVertex);
            }
        for (val vertex : graph.getVertices()) {
            tryAddEdges(vertex, gamePointAndVertex, graph, field);
        }
        return graph;
    }
}
