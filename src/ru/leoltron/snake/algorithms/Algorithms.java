package ru.leoltron.snake.algorithms;

import lombok.val;
import ru.leoltron.snake.algorithms.graph.Edge;
import ru.leoltron.snake.algorithms.graph.SimpleGraph;
import ru.leoltron.snake.algorithms.graph.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public final class Algorithms {
    public static ArrayList<Edge> getBridges(SimpleGraph graph){
        val bridges = new ArrayList<Edge>();
        val enterTime = new HashMap<Vertex, Integer>();
        val upTime = new HashMap<Vertex, Integer>();
        Integer currentTime = 0;
        for (val vertex : graph.getVertices()){
            if (enterTime.containsKey(vertex)) continue;
            findBridges(vertex, new Vertex(), enterTime, upTime, bridges, currentTime);
        }
        return bridges;
    }

    public static ArrayList<ArrayList<Vertex>> buildConnectedComponents(SimpleGraph graph){
        val used = new HashSet<Vertex>();
        val components = new ArrayList<ArrayList<Vertex>>();
        for (val vertex : graph.getVertices()){
            if (used.contains(vertex)) continue;
            val currentComponent = new ArrayList<Vertex>();
            findComponent(vertex, used, currentComponent);
            components.add(currentComponent);
        }
        return components;
    }

    private static void findBridges(Vertex current, Vertex parent, HashMap<Vertex, Integer> enterTime,
                                   HashMap<Vertex, Integer> upTime, ArrayList<Edge> bridges, Integer currentTime){
        enterTime.put(current, currentTime);
        upTime.put(current, currentTime++);
        for (val edge : current.getHeighbors()){
            val to = edge.getTo();
            if (to == parent) continue;
            if (enterTime.containsKey(to)){
                upTime.put(current, Math.min(upTime.get(current), enterTime.get(to)));
                continue;
            }
            findBridges(to, current, enterTime, upTime, bridges, currentTime);
            upTime.put(current, Math.min(upTime.get(current), upTime.get(to)));
            if (upTime.get(to) > enterTime.get(current))
                bridges.add(edge);
        }
    }

    private static void findComponent(Vertex vertex, HashSet<Vertex> used, ArrayList<Vertex> vertices){
        used.add(vertex);
        vertices.add(vertex);
        for (val edge : vertex.getHeighbors()){
            val to = edge.getTo();
            if (used.contains(to)) continue;
            findComponent(to, used, vertices);
        }
    }

}
