package ru.leoltron.snake.util.algorithms;

import lombok.val;
import ru.leoltron.snake.util.algorithms.graph.Edge;
import ru.leoltron.snake.util.algorithms.graph.SimpleGraph;
import ru.leoltron.snake.util.algorithms.graph.Vertex;
import sun.misc.Queue;

import java.util.HashMap;
import java.util.HashSet;

public final class Algorithms {
    public static HashSet<Edge> getBridges(SimpleGraph graph){
        val bridges = new HashSet<Edge>();
        val enterTime = new HashMap<Vertex, Integer>();
        val upTime = new HashMap<Vertex, Integer>();
        Integer currentTime = 0;
        for (val vertex : graph.getVertices()){
            if (enterTime.containsKey(vertex)) continue;
            findBridges(vertex, new Vertex(), enterTime, upTime, bridges, currentTime);
        }
        return bridges;
    }

    public static HashSet<HashSet<Vertex>> buildConnectedComponents(SimpleGraph graph){
        val used = new HashSet<Vertex>();
        val components = new HashSet<HashSet<Vertex>>();
        for (val vertex : graph.getVertices()){
            if (used.contains(vertex)) continue;
            val currentComponent = new HashSet<Vertex>();
            findComponent(vertex, used, currentComponent);
            components.add(currentComponent);
        }
        return components;
    }

    public static HashMap<Vertex, Integer> bfs(Vertex start) throws InterruptedException {
        val distances = new HashMap<Vertex, Integer>();
        distances.put(start, 0);
        Queue<Vertex> queue = new Queue<>();
        queue.enqueue(start);
        while (!queue.isEmpty()) {
            Vertex current = queue.dequeue();
            int currentDistance = distances.get(current);
            for (val edge : current.getHeighbors()){
                val to = edge.getTo();
                if (distances.containsKey(to))
                    continue;
                distances.put(to, currentDistance + 1);
                queue.enqueue(to);
            }
        }
        return distances;
    }

    private static void findBridges(Vertex current, Vertex parent, HashMap<Vertex, Integer> enterTime,
                                   HashMap<Vertex, Integer> upTime, HashSet<Edge> bridges, Integer currentTime){
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

    private static void findComponent(Vertex vertex, HashSet<Vertex> used, HashSet<Vertex> vertices){
        used.add(vertex);
        vertices.add(vertex);
        for (val edge : vertex.getHeighbors()){
            val to = edge.getTo();
            if (used.contains(to)) continue;
            findComponent(to, used, vertices);
        }
    }

}
