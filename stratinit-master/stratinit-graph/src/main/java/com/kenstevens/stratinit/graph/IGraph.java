package com.kenstevens.stratinit.graph;

import java.util.Iterator;

public interface IGraph<T> {
    int getVerticesNumber();

    void addVertex(T var1);

    void addEdge(T var1, T var2, double var3);

    void removeEdge(T var1, T var2);

    boolean edgeExist(T var1, T var2);

    boolean vertexExist(T var1);

    double getEdgeWeight(T var1, T var2);

    double getEdgeWeight(Path<T> var1);

    Iterator<T> getAdjacentVertices(T var1);

    Iterator<T> getPredecessors(T var1);
}
