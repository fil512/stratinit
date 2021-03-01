package com.kenstevens.stratinit.graph;

import java.util.NavigableSet;
import java.util.TreeSet;

public class PriorityQueue<P> {
    private final NavigableSet<QueueElement<P>> queue = new TreeSet();

    public PriorityQueue() {
    }

    public void clear() {
        this.queue.clear();
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public int getSize() {
        return this.queue.size();
    }

    public void insert(P element, double doublePriority) {
        int priority = (int) (doublePriority * 10000.0D);
        if (element == null) {
            throw new IllegalArgumentException("element must be not null");
        } else if (priority < 0) {
            throw new IllegalArgumentException("Illegal distance: " + priority);
        } else {
            QueueElement<P> queueElement = new QueueElement(element, priority);
            this.queue.add(queueElement);
        }
    }

    public P dequeueLowestPriorityElement() {
        if (!this.isEmpty()) {
            QueueElement<P> queueElement = this.queue.first();
            P element = queueElement.element;
            this.queue.remove(queueElement);
            return element;
        } else {
            return null;
        }
    }
}
