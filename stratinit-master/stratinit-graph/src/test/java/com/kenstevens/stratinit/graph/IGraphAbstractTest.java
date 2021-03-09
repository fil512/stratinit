package com.kenstevens.stratinit.graph;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

public abstract class IGraphAbstractTest {
    protected static final int VERTICES_NUMBER = 5;
    private static final String A = "A";
    private static final String B = "B";
    private static final String C = "C";
    private static final String D = "D";
    private static final String E = "E";
    private IGraph<String> graph;

    protected void setUp(IGraph<String> graph) {
        this.graph = graph;
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");
        graph.addEdge("A", "B", 5.0D);
        graph.addEdge("B", "C", 4.0D);
        graph.addEdge("C", "D", 8.0D);
        graph.addEdge("D", "C", 8.0D);
        graph.addEdge("D", "E", 6.0D);
        graph.addEdge("A", "D", 5.0D);
        graph.addEdge("C", "E", 2.0D);
        graph.addEdge("E", "B", 3.0D);
        graph.addEdge("A", "E", 7.0D);
    }

    @AfterEach
    protected void tearDown() {
    }

    @Test
    public void testGetVerticesNumber() {
        assertEquals(5, this.graph.getVerticesNumber());
    }

    @Test
    public void testAddVertex() {
        try {
            this.graph.addVertex("Z");
            fail("Should raise an Exception");
        } catch (Exception var2) {
            System.out.println(var2.toString());
        }

    }

    @Test
    public void testAddEdge() {
        try {
            this.graph.addEdge("Z", "B", 5.0D);
            fail("Should raise an Exception");
        } catch (Exception var4) {
            System.out.println(var4.toString());
        }

        try {
            this.graph.addEdge("A", "Z", 5.0D);
            fail("Should raise an Exception");
        } catch (Exception var3) {
            System.out.println(var3.toString());
        }

        try {
            this.graph.addEdge("A", "B", 0.0D);
            fail("Should raise an Exception");
        } catch (Exception var2) {
            System.out.println(var2.toString());
        }

    }

    @Test
    public void testRemoveEdge() {
        this.graph.addEdge("A", "C", 1.0D);
        this.graph.removeEdge("A", "C");
        assertTrue(!this.graph.edgeExist("A", "C"));
    }

    @Test
    public void testVertexExist() {
        assertTrue(this.graph.vertexExist("A"));
        assertTrue(this.graph.vertexExist("B"));
        assertTrue(this.graph.vertexExist("C"));
        assertTrue(this.graph.vertexExist("D"));
        assertTrue(this.graph.vertexExist("E"));
        assertTrue(!this.graph.vertexExist("Z"));
    }

    @Test
    public void testEdgeExist() {
        assertTrue(this.graph.edgeExist("A", "B"));
        assertTrue(this.graph.edgeExist("B", "C"));
        assertTrue(!this.graph.edgeExist("A", "C"));
    }

    @Test
    public void testGetEdgeWeight() {
        assertEquals(5.0D, this.graph.getEdgeWeight("A", "B"));
        assertEquals(4.0D, this.graph.getEdgeWeight("B", "C"));
        assertEquals(0.0D, this.graph.getEdgeWeight("A", "C"));
    }

    @Test
    public void testGetEdgeWeight_Path() {
        Path<String> p1 = new Path();
        p1.addVertex("A").addVertex("B").addVertex("C");
        assertEquals(9.0D, this.graph.getEdgeWeight(p1));
        Path<String> p2 = new Path();
        p2.addVertex("A").addVertex("D");
        assertEquals(5.0D, this.graph.getEdgeWeight(p2));
        Path<String> p3 = new Path();
        p3.addVertex("A").addVertex("D").addVertex("C");
        assertEquals(13.0D, this.graph.getEdgeWeight(p3));
        Path<String> p4 = new Path();
        p4.addVertex("A").addVertex("E").addVertex("B").addVertex("C").addVertex("D");
        assertEquals(22.0D, this.graph.getEdgeWeight(p4));
        Path<String> p5 = new Path();
        p5.addVertex("A").addVertex("E").addVertex("D");
        assertEquals(0.0D, this.graph.getEdgeWeight(p5));
    }

    @Test
    public void testGetAdjacentVertices() {
        String[] arrayA = new String[]{"B", "D", "E"};
        this.testIfAllArePresent(this.graph.getAdjacentVertices("A"), Arrays.asList(arrayA));
        String[] arrayB = new String[]{"C"};
        this.testIfAllArePresent(this.graph.getAdjacentVertices("B"), Arrays.asList(arrayB));
        String[] arrayC = new String[]{"D", "E"};
        this.testIfAllArePresent(this.graph.getAdjacentVertices("C"), Arrays.asList(arrayC));
        String[] arrayD = new String[]{"C"};
        this.testIfAllArePresent(this.graph.getAdjacentVertices("D"), Arrays.asList(arrayD));
        String[] arrayE = new String[]{"B"};
        this.testIfAllArePresent(this.graph.getAdjacentVertices("E"), Arrays.asList(arrayE));
    }

    @Test
    public void testGetPredecessors() {
        String[] arrayA = new String[0];
        this.testIfAllArePresent(this.graph.getPredecessors("A"), Arrays.asList(arrayA));
        String[] arrayB = new String[]{"A", "E"};
        this.testIfAllArePresent(this.graph.getPredecessors("B"), Arrays.asList(arrayB));
        String[] arrayC = new String[]{"B", "D"};
        this.testIfAllArePresent(this.graph.getPredecessors("C"), Arrays.asList(arrayC));
        String[] arrayD = new String[]{"A", "C"};
        this.testIfAllArePresent(this.graph.getPredecessors("D"), Arrays.asList(arrayD));
        this.testIfAllArePresent(this.graph.getPredecessors("E"), Arrays.asList(arrayD));
    }

    private void testIfAllArePresent(Iterator<String> iter, List<String> list) {
        Vector vertices = new Vector(list);

        while (iter.hasNext()) {
            String item = iter.next();
            vertices.remove(item);
        }

        assertTrue(vertices.isEmpty());
    }
}
