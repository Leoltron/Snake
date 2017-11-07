package ru.leoltron.snake.util.algorithms.graph;

public class SimpleGraph extends Graph {

    @Override
    public void addEdge(SimpleEdge edge) {
        if (!vertices.contains(edge.getFrom()))
            vertices.add(edge.getFrom());
        if (!vertices.contains(edge.getTo()))
            vertices.add(edge.getTo());
        edge.getFrom().addEdge(edge);
        edge.getTo().addEdge(edge.reversed());
    }
}
