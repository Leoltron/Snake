package ru.leoltron.snake.util.algorithms.graph;

import lombok.val;

public class SimpleGraph extends Graph {

    @Override
    public void addEdge(Edge simpleEdge) {
        val edge = (SimpleEdge) simpleEdge;
        if (!vertices.contains(edge.getFrom()))
            vertices.add(edge.getFrom());
        if (!vertices.contains(edge.getTo()))
            vertices.add(edge.getTo());
        edge.getFrom().addEdge(edge);
        edge.getTo().addEdge(edge.reversed());
    }
}
