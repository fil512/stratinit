package com.kenstevens.stratinit.graph;

import java.util.*;

public class AdjacencyMatrixGraph<T> implements IGraph<T> {
    private final int verticesNumber;
    private final Map<T, Integer> verticesMap;
    private final Vector<T> objectsArray;
    private final double[][] adjacencyMatrix;
    private final double[][] adjacencyMatrixInversed;
    private int indexCurrentVertex;

    public AdjacencyMatrixGraph(int verticesNumber) {
        if (verticesNumber < 1) {
            throw new IllegalArgumentException("Can't create graph. The verticesNumber must be > 0");
        } else {
            this.verticesNumber = verticesNumber;
            this.adjacencyMatrix = new double[verticesNumber][verticesNumber];
            this.adjacencyMatrixInversed = new double[verticesNumber][verticesNumber];
            this.verticesMap = new HashMap();
            this.objectsArray = new Vector();
            this.objectsArray.setSize(verticesNumber);
            this.indexCurrentVertex = 0;
        }
    }

    public int getVerticesNumber() {
        return this.verticesNumber;
    }

    public void addVertex(T vertex) {
        if (this.indexCurrentVertex >= this.verticesNumber) {
            throw new IllegalArgumentException("Can't add the vertex because all the vertices have been alreay added");
        } else {
            this.verticesMap.put(vertex, new Integer(this.indexCurrentVertex));
            this.objectsArray.set(this.indexCurrentVertex, vertex);
            ++this.indexCurrentVertex;
        }
    }

    public void addEdge(T startVertex, T destinationVertex, double weight) {
        int start = this.getVertexIndex(startVertex);
        int destination = this.getVertexIndex(destinationVertex);
        if (weight <= 0.0D) {
            throw new IllegalArgumentException("Invalid weight ! " + weight + ". Must be > 0");
        } else if (weight == 2.147483647E9D) {
            throw new IllegalArgumentException("Invalid weight ! " + weight + ". Must be < " + 2147483647);
        } else {
            this.adjacencyMatrix[start][destination] = weight;
            this.adjacencyMatrixInversed[destination][start] = weight;
        }
    }

    public void removeEdge(T startVertex, T destinationVertex) {
        int start = this.getVertexIndex(startVertex);
        int destination = this.getVertexIndex(destinationVertex);
        this.adjacencyMatrix[start][destination] = 0.0D;
        this.adjacencyMatrixInversed[destination][start] = 0.0D;
    }

    public boolean edgeExist(T startVertex, T destinationVertex) {
        int start = this.getVertexIndex(startVertex);
        int destination = this.getVertexIndex(destinationVertex);
        return this.adjacencyMatrix[start][destination] != 0.0D;
    }

    public boolean vertexExist(T vertex) {
        return this.verticesMap.containsKey(vertex);
    }

    public double getEdgeWeight(T startVertex, T destinationVertex) {
        int start = this.getVertexIndex(startVertex);
        int destination = this.getVertexIndex(destinationVertex);
        return this.adjacencyMatrix[start][destination];
    }

    public double getEdgeWeight(Path<T> path) {
        if (path.getLength() < 1) {
            return 0.0D;
        } else {
            int total = 0;

            for (int i = 0; i < path.getLength(); ++i) {
                double edgeWeigth = this.getEdgeWeight(path.get(i), path.get(i + 1));
                if (edgeWeigth == 0.0D) {
                    return 0.0D;
                }

                total = (int) ((double) total + edgeWeigth);
            }

            return total;
        }
    }

    public Iterator<T> getAdjacentVertices(T vertex) {
        return this.getAdjacentsFromMatrix(vertex, this.adjacencyMatrix).iterator();
    }

    public Iterator<T> getPredecessors(T vertex) {
        return this.getAdjacentsFromMatrix(vertex, this.adjacencyMatrixInversed).iterator();
    }

    private ArrayList<T> getAdjacentsFromMatrix(T vertex, double[][] matrix) {
        int start = this.getVertexIndex(vertex);
        ArrayList<T> adjacentVertices = new ArrayList();

        for (int i = 0; i < matrix.length; ++i) {
            double weight = matrix[start][i];
            if (weight != 0.0D) {
                adjacentVertices.add(this.getVertexObject(i));
            }
        }

        return adjacentVertices;
    }

    private int getVertexIndex(T vertex) {
        if (!this.verticesMap.containsKey(vertex)) {
            throw new IllegalArgumentException("The  vertex ! " + vertex + " does not exist in the graph.");
        } else {
            return this.verticesMap.get(vertex);
        }
    }

    private T getVertexObject(int index) {
        return this.objectsArray.get(index);
    }
}
