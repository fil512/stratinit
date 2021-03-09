package com.kenstevens.stratinit.graph;

import java.util.NavigableSet;
import java.util.TreeSet;

public class DistanceQueue {
    private final NavigableSet<Object> queue = new TreeSet();

    public DistanceQueue() {
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

    public void insert(Object element, double distance) {
        if (element == null) {
            throw new IllegalArgumentException("element must be not null");
        } else if (distance < 0.0D) {
            throw new IllegalArgumentException("Illegal distance: " + distance);
        } else {
            DistanceQueue.QueueElement queueElement = new DistanceQueue.QueueElement(element, distance);
            this.queue.add(queueElement);
        }
    }

    public Object dequeueLowestPriorityElement() {
        if (!this.isEmpty()) {
            DistanceQueue.QueueElement queueElement = (DistanceQueue.QueueElement) this.queue.first();
            Object element = queueElement.getElement();
            this.queue.remove(queueElement);
            return element;
        } else {
            return null;
        }
    }

    public class QueueElement implements Comparable<Object> {
        private final Object element;
        private final double distance;

        public QueueElement(Object element, double distance) {
            this.element = element;
            this.distance = distance;
        }

        public double getDistance() {
            return this.distance;
        }

        public Object getElement() {
            return this.element;
        }

        public int compareTo(Object o) {
            double distanceCompared = ((DistanceQueue.QueueElement) o).getDistance();
            if (this.distance == distanceCompared) {
                return this.element.equals(((DistanceQueue.QueueElement) o).getElement()) ? 0 : -1;
            } else {
                return this.distance < distanceCompared ? -1 : 1;
            }
        }

        public boolean equals(Object o) {
            return this.element.equals(((DistanceQueue.QueueElement) o).getElement()) && ((DistanceQueue.QueueElement) o).getDistance() == this.distance;
        }
    }
}
