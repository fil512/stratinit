package com.kenstevens.stratinit.graph;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DistanceQueueTest {
    private static final String A = "A";
    private static final String B = "B";
    private static final String C = "C";
    private static final String D = "D";
    private static final String E = "E";
    private DistanceQueue queue;

    @BeforeEach
    protected void setUp() {
        this.queue = new DistanceQueue();
        this.addData();
    }

    private void addData() {
        this.queue.insert("B", 5.0D);
        this.queue.insert("C", 5.0D);
        this.queue.insert("D", 5.0D);
        this.queue.insert("E", 7.0D);
    }

    @AfterEach
    protected void tearDown() {
    }

    @Test
    public void testClear() {
        this.queue.clear();
        assertTrue(this.queue.isEmpty());
        this.addData();
    }

    @Test
    public void testDequeueLowestPriorityElement() {
        assertEquals("D", this.queue.dequeueLowestPriorityElement());
        assertEquals("C", this.queue.dequeueLowestPriorityElement());
        assertEquals("B", this.queue.dequeueLowestPriorityElement());
        assertEquals("E", this.queue.dequeueLowestPriorityElement());
        assertNull(this.queue.dequeueLowestPriorityElement());
        assertTrue(this.queue.isEmpty());
    }

    @Test
    public void testInsert() {
        try {
            this.queue.insert(null, 1.0D);
            fail("Should raise an Exception");
        } catch (Exception var3) {
            System.out.println(var3.toString());
        }

        try {
            this.queue.insert("A", -1.0D);
            fail("Should raise an Exception");
        } catch (Exception var2) {
            System.out.println(var2.toString());
        }

    }

    @Test
    public void testIsEmpty() {
        DistanceQueue q = new DistanceQueue();
        assertTrue(q.isEmpty());
    }
}
