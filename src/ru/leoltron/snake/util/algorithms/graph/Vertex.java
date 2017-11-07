package ru.leoltron.snake.util.algorithms.graph;

import java.util.HashSet;

public class Vertex {

    private final HashSet<Edge> neighbors;

    public Vertex(){
        neighbors = new HashSet<>();
    }
    public HashSet<Edge> getHeighbors(){
        return neighbors;
    }
    public void addEdge(Edge edge){
        neighbors.add(edge);
    }
}
