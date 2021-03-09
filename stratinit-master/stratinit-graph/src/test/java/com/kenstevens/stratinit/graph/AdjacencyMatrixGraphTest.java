package com.kenstevens.stratinit.graph;

import org.junit.jupiter.api.BeforeEach;

public class AdjacencyMatrixGraphTest extends IGraphAbstractTest {
    private AdjacencyMatrixGraph<String> graph;

    @BeforeEach
    protected void setUp() {
        this.graph = new AdjacencyMatrixGraph(5);
        super.setUp(this.graph);
    }
}
