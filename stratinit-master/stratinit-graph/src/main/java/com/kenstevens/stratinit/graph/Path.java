package com.kenstevens.stratinit.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Path<T> implements Cloneable {
    private List<T> verticesList;

    public Path() {
        this.verticesList = new ArrayList();
    }

    public Path(List<T> list) {
        this.verticesList = list;
    }

    public int getLength() {
        return this.verticesList.size() - 1;
    }

    public Path<T> addVertex(T vertex) {
        this.verticesList.add(vertex);
        return this;
    }

    public Path<T> addPath(Path<T> path) {
        this.verticesList.addAll(path.verticesList);
        return this;
    }

    public T get(int index) {
        return this.verticesList.get(index);
    }

    public T getLast() {
        return this.verticesList.get(this.verticesList.size() - 1);
    }

    public Path<T> clone() {
        Path<T> newInstance = null;
        newInstance = new Path();
        newInstance.addPath(this);
        newInstance.verticesList = new ArrayList(this.verticesList);
        return newInstance;
    }

    public String toString() {
        String s = "";
        Iterator<T> iter = this.verticesList.iterator();
        Object item;
        if (iter.hasNext()) {
            item = iter.next();
            s = s + item.toString();
        }

        while (iter.hasNext()) {
            item = iter.next();
            s = s + "-" + item.toString();
        }

        return s;
    }
}

