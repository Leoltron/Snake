package ru.leoltron.snake.util.algorithms.graph;

import lombok.Getter;

public class SimpleEdge implements Edge{
    @Getter
    private Vertex from;
    @Getter
    private Vertex to;
    public SimpleEdge(Vertex from, Vertex to){
        this.from = from;
        this.to = to;
    }
    public SimpleEdge reversed(){
        return new SimpleEdge(to, from);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != SimpleEdge.class)
            return false;
        SimpleEdge edge = (SimpleEdge)obj;
        return edge.getFrom() == from && edge.getTo() == to || edge.getFrom() == to && edge.getTo() == from;
    }

    @Override
    public int hashCode() {
        return from.hashCode() ^ to.hashCode();
    }
}
