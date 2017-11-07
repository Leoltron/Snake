package ru.leoltron.snake.util.algorithms.graph;

import java.util.HashSet;

public abstract class Graph {
    protected final HashSet<Vertex> vertices;

    protected Graph() {
        this.vertices = new HashSet<>();
    }

    public HashSet<Vertex> getVertices() {
        return vertices;
    }

    public abstract void addEdge(SimpleEdge edge);
}
