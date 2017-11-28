package ru.leoltron.snake.util.algorithms.graph;

public class WeightedVertex extends Vertex {
    private int weight;
    public WeightedVertex(int weight){
        super();
        this.weight = weight;
    }
    public int getWeight() {
        return weight;
    }
}
