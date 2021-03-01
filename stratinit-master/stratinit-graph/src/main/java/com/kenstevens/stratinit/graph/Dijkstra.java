package com.kenstevens.stratinit.graph;

import java.util.*;

public class Dijkstra<T> {
    private static final double INFINITE = 1.7976931348623157E308D;
    private final IGraph<T> graph;
    private final Set<T> determinedVerticesSet;
    private final PriorityQueue<T> remainingVerticesQueue;
    private final Map<T, Double> shortestPathMap;
    private final Map<T, T> predecessorsMap;

    public Dijkstra(IGraph<T> graph) {
        this.graph = graph;
        int verticesNumber = graph.getVerticesNumber();
        this.determinedVerticesSet = new HashSet(verticesNumber);
        this.remainingVerticesQueue = new PriorityQueue();
        this.shortestPathMap = new HashMap(verticesNumber);
        this.predecessorsMap = new HashMap(verticesNumber);
    }

    private void runAlgorihtm(T sourceVertex, T destinationVertex) {
        this.shortestPathMap.clear();
        this.predecessorsMap.clear();
        this.determinedVerticesSet.clear();
        this.remainingVerticesQueue.clear();
        this.shortestPathMap.put(sourceVertex, new Double(0.0D));
        this.remainingVerticesQueue.insert(sourceVertex, 0.0D);

        while (!this.remainingVerticesQueue.isEmpty()) {
            T closest = this.remainingVerticesQueue.dequeueLowestPriorityElement();
            if (closest.equals(destinationVertex)) {
                break;
            }

            this.determinedVerticesSet.add(closest);
            this.relax(closest);
        }

    }

    private void relax(T vertex) {
        Iterator<T> adjacentVertices = this.graph.getAdjacentVertices(vertex);

        while (adjacentVertices.hasNext()) {
            T adjVertex = adjacentVertices.next();
            if (!this.determinedVerticesSet.contains(adjVertex)) {
                double distance = this.getShortestPathFromSource(vertex) + this.graph.getEdgeWeight(vertex, adjVertex);
                if (this.getShortestPathFromSource(adjVertex) > distance) {
                    this.setShortestPathFromStart(adjVertex, distance);
                    this.predecessorsMap.put(adjVertex, vertex);
                    this.remainingVerticesQueue.insert(adjVertex, distance);
                }
            }
        }

    }

    private double getShortestPathFromSource(T vertex) {
        return this.shortestPathMap.containsKey(vertex) ? this.shortestPathMap.get(vertex) : 1.7976931348623157E308D;
    }

    private void setShortestPathFromStart(T vertex, double path) {
        this.shortestPathMap.put(vertex, new Double(path));
    }

    public double getShortestWeightDistance(T start, T destination) {
        if (start.equals(destination)) {
            return 0.0D;
        } else {
            Path<T> shortestPath = this.getShortestPath(start, destination);
            return this.graph.getEdgeWeight(shortestPath);
        }
    }

    public Path<T> getShortestPath(T start, T destination) {
        this.checkVertexExist(start);
        this.checkVertexExist(destination);
        this.runAlgorihtm(start, destination);
        if (!start.equals(destination)) {
            return this.buildShortestPath(start, destination);
        } else {
            PriorityQueue<T> solutionsPQ = new PriorityQueue();
            Iterator<T> iter = this.graph.getAdjacentVertices(start);

            while (iter.hasNext()) {
                T vertex = iter.next();
                double distFromDestVertex = this.graph.getEdgeWeight(start, vertex);
                this.runAlgorihtm(vertex, destination);
                solutionsPQ.insert(vertex, distFromDestVertex + this.getShortestPathFromSource(destination));
            }

            Path<T> path = new Path();
            path.addVertex(start);
            path.addPath(this.buildShortestPath(solutionsPQ.dequeueLowestPriorityElement(), destination));
            return path;
        }
    }

    private Path<T> buildShortestPath(T start, T destination) {
        Path<T> path = new Path();
        if (this.getShortestPathFromSource(destination) == 1.7976931348623157E308D) {
            return path;
        } else {
            ArrayList<T> pathList = new ArrayList();
            T predecessor = destination;

            do {
                pathList.add(predecessor);
                predecessor = this.predecessorsMap.get(predecessor);
            } while (predecessor != null && !predecessor.equals(start));

            pathList.add(start);
            Collections.reverse(pathList);
            return new Path(pathList);
        }
    }

    private void checkVertexExist(T vertex) {
        if (!this.graph.vertexExist(vertex)) {
            throw new IllegalArgumentException("The  vertex ! " + vertex + " does not exist in the graph.");
        }
    }
}
