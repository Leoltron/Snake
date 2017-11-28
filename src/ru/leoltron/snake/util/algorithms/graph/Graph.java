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

    public void addVertex(Vertex vertex){
        vertices.add(vertex);
    }

    public boolean hasVertex(Vertex vertex){
        return vertices.contains(vertex);
    }

    public abstract void addEdge(Edge edge);
}
