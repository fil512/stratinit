package com.kenstevens.stratinit.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PathFinder<T> {
    private final IGraph<T> graph;
    private final Dijkstra<T> dijkstra;
    private final ArrayList<Path<T>> solutionsList;
    private T destination;
    private int maxLength;
    private double maxDistance;

    public PathFinder(IGraph<T> graph) {
        this.graph = graph;
        this.dijkstra = new Dijkstra(graph);
        this.solutionsList = new ArrayList();
    }

    public double getShortestWeightDistance(T start, T destination) {
        return this.dijkstra.getShortestWeightDistance(start, destination);
    }

    public Path<T> getShortestPath(T start, T destination) {
        return this.dijkstra.getShortestPath(start, destination);
    }

    public List<Path<T>> findPathsWithMaximumLength(T start, T destination, int maxLength) {
        this.findPaths(start, destination, maxLength);
        return this.solutionsList;
    }

    public List<Path<T>> findPathsWithExactLength(T start, T destination, int exactLength) {
        this.findPaths(start, destination, exactLength);
        ArrayList<Path<T>> filteredSolution = new ArrayList();
        Iterator iter = this.solutionsList.iterator();

        while (iter.hasNext()) {
            Path<T> path = (Path) iter.next();
            if (path.getLength() == exactLength) {
                filteredSolution.add(path);
            }
        }

        return filteredSolution;
    }

    public List<Path<T>> findPathsWithMaximumDistance(T start, T destination, double maxDistance) {
        this.destination = destination;
        this.maxDistance = maxDistance;
        Path<T> rootPath = new Path();
        rootPath.addVertex(start);
        this.solutionsList.clear();
        this.searchDistance(rootPath);
        return this.solutionsList;
    }

    private void findPaths(T start, T destination, int maxLength) {
        if (!this.graph.vertexExist(start)) {
            throw new IllegalArgumentException("The  vertex ! " + start + " does not exist in the graph.");
        } else if (!this.graph.vertexExist(destination)) {
            throw new IllegalArgumentException("The  vertex ! " + destination + " does not exist in the graph.");
        } else {
            this.destination = destination;
            this.maxLength = maxLength;
            Path<T> rootPath = new Path();
            rootPath.addVertex(start);
            this.solutionsList.clear();
            this.searchLength(rootPath);
        }
    }

    private void searchLength(Path<T> path) {
        if (path.getLength() < this.maxLength) {
            T lastVertex = path.getLast();

            Path newPath;
            for (Iterator<T> iterAdjacents = this.graph.getAdjacentVertices(lastVertex); iterAdjacents.hasNext(); this.searchLength(newPath)) {
                T adjacentVertex = iterAdjacents.next();
                newPath = path.clone();
                newPath.addVertex(adjacentVertex);
                if (adjacentVertex.equals(this.destination)) {
                    this.solutionsList.add(newPath);
                }
            }
        }

    }

    private void searchDistance(Path<T> path) {
        T lastVertex = path.getLast();
        Path newPath;
        if (this.graph.getEdgeWeight(path) + this.dijkstra.getShortestWeightDistance(lastVertex, this.destination) <= this.maxDistance) {
            for (Iterator<T> iterAdjacents = this.graph.getAdjacentVertices(lastVertex); iterAdjacents.hasNext(); this.searchDistance(newPath)) {
                T adjacentVertex = iterAdjacents.next();
                newPath = path.clone();
                newPath.addVertex(adjacentVertex);
                if (adjacentVertex.equals(this.destination) && this.graph.getEdgeWeight(newPath) <= this.maxDistance) {
                    this.solutionsList.add(newPath);
                }
            }
        }

    }
}
