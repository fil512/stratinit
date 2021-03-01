package com.kenstevens.stratinit.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// FIXME add the rest of the tests
public class DijkstraTest {
    public static final String A = "A";
    public static final String B = "B";
    public static final String C = "C";
    public static final String D = "D";
    public static final String E = "E";
    private static final String F = "F";
    private static final String G = "G";
    private static final String H = "H";
    private Dijkstra<String> dij;
    private MockGraph graph;

    @BeforeEach
    protected void setUp() {
        this.graph = new MockGraph();
        this.graph.buildGraphTest();
        this.dij = new Dijkstra(this.graph);
    }

    @Test
    public void testGetShortestWeightDistance() {
        assertEquals(9.0D, this.dij.getShortestWeightDistance("A", "C"));
        assertEquals(0.0D, this.dij.getShortestWeightDistance("B", "B"));
    }

    @Test
    public void testBUGFindShortestPath() {
        AdjacencyMatrixGraph<String> buggedGraph = new AdjacencyMatrixGraph(8);
        buggedGraph.addVertex("A");
        buggedGraph.addVertex("B");
        buggedGraph.addVertex("C");
        buggedGraph.addVertex("D");
        buggedGraph.addVertex("E");
        buggedGraph.addVertex("F");
        buggedGraph.addVertex("G");
        buggedGraph.addVertex("H");
        buggedGraph.addEdge("A", "B", 1.0D);
        buggedGraph.addEdge("B", "A", 1.0D);
        buggedGraph.addEdge("B", "C", 1.0D);
        buggedGraph.addEdge("C", "B", 1.0D);
        buggedGraph.addEdge("C", "D", 1.0D);
        buggedGraph.addEdge("D", "C", 1.0D);
        buggedGraph.addEdge("C", "E", 1.0D);
        buggedGraph.addEdge("E", "C", 1.0D);
        buggedGraph.addEdge("C", "F", 1.0D);
        buggedGraph.addEdge("F", "C", 1.0D);
        buggedGraph.addEdge("F", "G", 1.0D);
        buggedGraph.addEdge("G", "F", 1.0D);
        buggedGraph.addEdge("F", "H", 1.0D);
        buggedGraph.addEdge("H", "F", 1.0D);
        this.dij = new Dijkstra(buggedGraph);
        assertEquals("A-B-C-F-H", this.dij.getShortestPath("A", "H").toString());
    }
}

