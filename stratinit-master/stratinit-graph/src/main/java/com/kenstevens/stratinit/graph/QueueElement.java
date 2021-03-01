package com.kenstevens.stratinit.graph;

class QueueElement<Q> implements Comparable<QueueElement<Q>> {
    public Q element;
    public int priority;

    public QueueElement(Q element, int priority) {
        this.element = element;
        this.priority = priority;
    }

    public int compareTo(QueueElement<Q> o) {
        int priorityOther = o.priority;
        if (this.priority == priorityOther) {
            return this.element.equals(o.element) ? 0 : -1;
        } else {
            return this.priority < priorityOther ? -1 : 1;
        }
    }
}
