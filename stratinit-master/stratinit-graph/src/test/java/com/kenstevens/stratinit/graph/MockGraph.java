package com.kenstevens.stratinit.graph;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MockGraph implements IGraph<String> {
    public static final String A = "A";
    public static final String B = "B";
    public static final String C = "C";
    public static final String D = "D";
    public static final String E = "E";
    private static final String AB = "AB";
    private static final String BC = "BC";
    private static final String CD = "CD";
    private static final String DC = "DC";
    private static final String DE = "DE";
    private static final String AD = "AD";
    private static final String CE = "CE";
    private static final String EB = "EB";
    private static final String AE = "AE";
    private static final int VERTICES_NUMBER = 5;
    private final List<String> pathsList = new ArrayList();
    private int indexCurrentVertex = 0;

    public MockGraph() {
    }

    public void buildGraphTest() {
        this.addVertex("A");
        this.addVertex("B");
        this.addVertex("C");
        this.addVertex("D");
        this.addVertex("E");
        this.addEdge("A", "B", 5.0D);
        this.addEdge("B", "C", 4.0D);
        this.addEdge("C", "D", 8.0D);
        this.addEdge("D", "C", 8.0D);
        this.addEdge("D", "E", 6.0D);
        this.addEdge("A", "D", 5.0D);
        this.addEdge("C", "E", 2.0D);
        this.addEdge("E", "B", 3.0D);
        this.addEdge("A", "E", 7.0D);
    }

    public int getVerticesNumber() {
        return 5;
    }

    public void addVertex(String vertex) {
        if (this.indexCurrentVertex >= 5) {
            throw new IllegalArgumentException("Can't add the vertex because all the vertices have been alreay added");
        } else {
            ++this.indexCurrentVertex;
        }
    }

    public void addEdge(String startVertex, String destinationVertex, double weight) {
        if (this.vertexExist(startVertex) && this.vertexExist(destinationVertex)) {
            if (weight <= 0.0D) {
                throw new IllegalArgumentException("Invalid weight ! " + weight + ". Must be > 0");
            } else {
                this.pathsList.add(startVertex + destinationVertex);
            }
        } else {
            throw new IllegalArgumentException("The  vertex(ices) do not exist in the graph.");
        }
    }

    public void removeEdge(String startVertex, String destinationVertex) {
        this.pathsList.remove(startVertex + destinationVertex);
    }

    public boolean edgeExist(String startVertex, String destinationVertex) {
        return this.pathsList.contains(startVertex + destinationVertex);
    }

    public boolean vertexExist(String vertex) {
        return vertex.equals("A") || vertex.equals("B") || vertex.equals("C") || vertex.equals("D") || vertex.equals("E");
    }

    public double getEdgeWeight(String startVertex, String destinationVertex) {
        if (this.edgeExist(startVertex, destinationVertex)) {
            String path = startVertex + destinationVertex;
            if (path.equals("AB")) {
                return 5.0D;
            } else if (path.equals("BC")) {
                return 4.0D;
            } else if (path.equals("CD")) {
                return 8.0D;
            } else if (path.equals("DC")) {
                return 8.0D;
            } else if (path.equals("DE")) {
                return 6.0D;
            } else if (path.equals("AD")) {
                return 5.0D;
            } else if (path.equals("CE")) {
                return 2.0D;
            } else if (path.equals("EB")) {
                return 3.0D;
            } else {
                return path.equals("AE") ? 7.0D : 0.0D;
            }
        } else {
            return 0.0D;
        }
    }

    public double getEdgeWeight(Path<String> verticesList) {
        if ("B-C-E-B".equals(verticesList.toString())) {
            return 9.0D;
        } else if ("A-B-C".equals(verticesList.toString())) {
            return 9.0D;
        } else if ("A-D".equals(verticesList.toString())) {
            return 5.0D;
        } else if ("A-D-C".equals(verticesList.toString())) {
            return 13.0D;
        } else if ("A-E-B-C-D".equals(verticesList.toString())) {
            return 22.0D;
        } else {
            return "A-E-D".equals(verticesList.toString()) ? 0.0D : 0.0D;
        }
    }

    public Iterator<String> getAdjacentVertices(String vertex) {
        List<String> adjacentVertices = new ArrayList();
        if (vertex.equals("A")) {
            adjacentVertices.add("B");
            adjacentVertices.add("D");
            adjacentVertices.add("E");
        }

        if (vertex.equals("B")) {
            adjacentVertices.add("C");
        }

        if (vertex.equals("C")) {
            adjacentVertices.add("D");
            adjacentVertices.add("E");
        }

        if (vertex.equals("D")) {
            adjacentVertices.add("C");
            adjacentVertices.add("E");
        }

        if (vertex.equals("E")) {
            adjacentVertices.add("B");
        }

        return adjacentVertices.iterator();
    }

    public Iterator<String> getPredecessors(String vertex) {
        List<String> adjacentVertices = new ArrayList();
        vertex.equals("A");
        if (vertex.equals("B")) {
            adjacentVertices.add("A");
            adjacentVertices.add("E");
        }

        if (vertex.equals("C")) {
            adjacentVertices.add("B");
            adjacentVertices.add("D");
        }

        if (vertex.equals("D")) {
            adjacentVertices.add("A");
            adjacentVertices.add("C");
        }

        if (vertex.equals("E")) {
            adjacentVertices.add("A");
            adjacentVertices.add("C");
            adjacentVertices.add("D");
        }

        return adjacentVertices.iterator();
    }
}

