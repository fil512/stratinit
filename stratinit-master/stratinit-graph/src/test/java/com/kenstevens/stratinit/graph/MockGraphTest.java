package com.kenstevens.stratinit.graph;

import org.junit.jupiter.api.BeforeEach;

public class MockGraphTest extends IGraphAbstractTest {
    private MockGraph graph;

    @BeforeEach
    protected void setUp() {
        this.graph = new MockGraph();
        super.setUp(this.graph);
    }
}
