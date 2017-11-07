package ru.leoltron.snaketests;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import ru.leoltron.snake.algorithms.Algorithms;
import ru.leoltron.snake.algorithms.graph.Edge;
import ru.leoltron.snake.algorithms.graph.SimpleEdge;
import ru.leoltron.snake.algorithms.graph.SimpleGraph;
import ru.leoltron.snake.algorithms.graph.Vertex;
import ru.leoltron.snake.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

public class AlgorithmsTest {
    private ArrayList<Vertex> vertices = new ArrayList<>();

    @Before
    public void initialize(){
        vertices.clear();
        for (int i = 0; i < 10; i++)
            vertices.add(new Vertex());
    }

    private void addEdge(SimpleGraph graph, int a, int b){
        graph.addEdge(new SimpleEdge(vertices.get(a), vertices.get(b)));
    }

    @Test
    public void testOneConnectedComponent(){
        SimpleGraph graph = new SimpleGraph();
        addEdge(graph, 0, 1);
        addEdge(graph, 0, 2);
        val result = Algorithms.buildConnectedComponents(graph);
        val expected = new HashSet<HashSet<Vertex>>();
        expected.add(new HashSet<>(Arrays.asList(vertices.get(0), vertices.get(1), vertices.get(2))));
        assertEquals(expected, result);
    }

    @Test
    public void testTwoConnectedComponents(){
        SimpleGraph graph = new SimpleGraph();
        addEdge(graph, 0, 1);
        addEdge(graph, 2, 3);
        val result = Algorithms.buildConnectedComponents(graph);
        assertEquals(2, Algorithms.buildConnectedComponents(graph).size());
        val expectedFirstComponent = new HashSet<Vertex>(Arrays.asList(vertices.get(0), vertices.get(1)));
        val expectedSecondComponent = new HashSet<Vertex>(Arrays.asList(vertices.get(2), vertices.get(3)));
        val expected = new HashSet<HashSet<Vertex>>();
        expected.add(expectedFirstComponent);
        expected.add(expectedSecondComponent);
        assertEquals(expected, result);
    }

    @Test
    public void testConnectedComponents(){
        SimpleGraph graph = new SimpleGraph();
        for (val vertex: vertices )
            graph.getVertices().add(vertex);
        assertEquals(vertices.size(), Algorithms.buildConnectedComponents(graph).size());
    }

    @Test
    public void testSimpleBridge(){
        SimpleGraph graph = new SimpleGraph();
        addEdge(graph, 0, 1);
        val result = Algorithms.getBridges(graph);
        assertEquals(1, result.size());
        val expected = new HashSet<SimpleEdge>();
        expected.add(new SimpleEdge(vertices.get(0), vertices.get(1)));
        assertEquals(expected, result);
    }

    @Test
    public void testGraphWithoutBridges(){
        SimpleGraph graph = new SimpleGraph();
        addEdge(graph,0, 1);
        addEdge(graph, 1, 2);
        addEdge(graph, 2, 3);
        addEdge(graph, 3, 0);
        assertEquals(0, Algorithms.getBridges(graph).size());
    }

    @Test
    public void testBridges(){
        SimpleGraph graph = new SimpleGraph();
        addEdge(graph,0, 1);
        addEdge(graph, 1, 2);
        addEdge(graph, 2, 0);
        addEdge(graph, 0, 3);
        addEdge(graph, 3, 4);
        addEdge(graph, 4, 5);
        addEdge(graph, 5, 3);
        val result = Algorithms.getBridges(graph);
        val expected = new HashSet<SimpleEdge>();
        expected.add(new SimpleEdge(vertices.get(0), vertices.get(3)));
        assertEquals(expected, result);
    }
}