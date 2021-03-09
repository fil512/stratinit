package com.kenstevens.stratinit.graph;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathFinderTest {
    protected static final int VERTICES_NUMBER = 5;
    private static final String A = "A";
    private static final String B = "B";
    private static final String C = "C";
    private static final String D = "D";
    private static final String E = "E";
    private PathFinder<String> finder;
    private AdjacencyMatrixGraph<String> graph;

    @BeforeEach
    protected void setUp() {
        this.graph = new AdjacencyMatrixGraph(5);
        this.graph.addVertex("A");
        this.graph.addVertex("B");
        this.graph.addVertex("C");
        this.graph.addVertex("D");
        this.graph.addVertex("E");
        this.graph.addEdge("A", "B", 5.0D);
        this.graph.addEdge("B", "C", 4.0D);
        this.graph.addEdge("C", "D", 8.0D);
        this.graph.addEdge("D", "C", 8.0D);
        this.graph.addEdge("D", "E", 6.0D);
        this.graph.addEdge("A", "D", 5.0D);
        this.graph.addEdge("C", "E", 2.0D);
        this.graph.addEdge("E", "B", 3.0D);
        this.graph.addEdge("A", "E", 7.0D);
        this.finder = new PathFinder(this.graph);
    }

    @AfterEach
    protected void tearDown() {
    }

    @Test
    public void testFindPathsWithExactLength() {
        List<Path<String>> solutions = this.finder.findPathsWithExactLength("A", "C", 4);
        Iterator<Path<String>> iter = solutions.iterator();
        System.out.println("Solutions for A, C with exact length = 4");

        while (iter.hasNext()) {
            String sol = iter.next().toString();
            assertTrue(sol.equals("A-B-C-D-C") || sol.equals("A-D-C-D-C") || sol.equals("A-D-E-B-C"));
            System.out.println(sol);
        }

    }

    @Test
    public void testFindPathsWithMaximumLength() {
        List<Path<String>> solutions = this.finder.findPathsWithMaximumLength("C", "C", 3);
        Iterator<Path<String>> iter = solutions.iterator();
        System.out.println("Solutions for C, C with maximum length = 3");

        while (iter.hasNext()) {
            String sol = iter.next().toString();
            assertTrue(sol.equals("C-D-C") || sol.equals("C-E-B-C"));
            System.out.println(sol);
        }

    }

    @Test
    public void testFindPathsWithMaximumDistance() {
        List<Path<String>> solutions = this.finder.findPathsWithMaximumDistance("C", "C", 29.0D);
        Iterator<Path<String>> iter = solutions.iterator();
        System.out.println("Solutions for C, C with maximum distance < 30");

        while (iter.hasNext()) {
            Path<String> p = iter.next();
            System.out.println(p + " Distance : " + this.graph.getEdgeWeight(p));
        }

    }
}
