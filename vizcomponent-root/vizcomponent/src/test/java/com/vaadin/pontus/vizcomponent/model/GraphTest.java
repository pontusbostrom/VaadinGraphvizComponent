package com.vaadin.pontus.vizcomponent.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.vaadin.pontus.vizcomponent.model.Subgraph.Edge;
import com.vaadin.pontus.vizcomponent.model.Subgraph.EdgeFactory;

public class GraphTest {

    @Test
    public void testAddNodes() {
        Graph graph = new Graph("test", Graph.DIGRAPH);
        Graph.Node node1 = new Graph.Node("1");
        Graph.Node node2 = new Graph.Node("2");

        graph.addNode(node1);
        graph.addNode(node2);

        Set<Graph.Node> nodes = graph.getNodes();

        assertTrue(nodes.size() == 2);
        assertTrue(nodes.contains(node1));
        assertTrue(nodes.contains(node2));
        assertEquals(graph.getNode("1"), node1);
        assertEquals(graph.getNode("2"), node2);
        assertNull(graph.getNode("3"));
        assertNull(graph.getNode(null));
        assertTrue(graph.getEdges().isEmpty());
        assertNull(graph.getEdge("1"));

    }

    @Test
    public void testAddEdgeNodesNotInGraph() {
        Graph graph = new Graph("test", Graph.DIGRAPH);
        Graph.Node node1 = new Graph.Node("1");
        Graph.Node node2 = new Graph.Node("2");

        Edge e = graph.addEdge(node1, node2);

        Set<Graph.Node> nodes = graph.getNodes();

        assertTrue(nodes.size() == 2);
        assertTrue(nodes.contains(node1));
        assertTrue(nodes.contains(node2));
        assertTrue(graph.getEdges().size() == 1);
        assertTrue(graph.getEdges().contains(e));
        assertEquals(graph.getEdge(node1, node2), e);
        assertNull(graph.getEdge(node2, node1));
        assertEquals(graph.getEdge(e.getId()), e);
        assertNull(graph.getEdge(null));

    }

    @Test
    public void testAddEdgeNodesInGraph() {
        Graph graph = new Graph("test", Graph.DIGRAPH);
        Graph.Node node1 = new Graph.Node("1");
        Graph.Node node2 = new Graph.Node("2");

        graph.addNode(node1);
        graph.addNode(node2);
        Edge e = graph.addEdge(node1, node2);

        Set<Graph.Node> nodes = graph.getNodes();

        assertTrue(nodes.size() == 2);
        assertTrue(nodes.contains(node1));
        assertTrue(nodes.contains(node2));
        assertEquals(graph.getEdges().size(), 1);
        assertTrue(graph.getEdges().contains(e));
        assertEquals(graph.getEdge(node1, node2), e);

    }

    @Test
    public void testAddEdgeWithEdgeSubclass() {

        Graph graph = new Graph("test", Graph.DIGRAPH);
        graph.setEdgeFactory(new EdgeFactory() {

            @Override
            public Edge newInstance() {

                return new CustomEdge();
            }

        });
        Graph.Node node1 = new Graph.Node("1");
        Graph.Node node2 = new Graph.Node("2");

        graph.addNode(node1);
        graph.addNode(node2);

        graph.addEdge(node1, node2);

        Set<Edge> edges = graph.getEdges();
        assertEquals(1, edges.size());
        for (Edge ed : edges) {
            assertTrue(ed instanceof CustomEdge);
        }

    }

    @Test
    public void testCreateSubGraphNodeInGraph() {
        Graph graph = new Graph("test", Graph.DIGRAPH);

        Subgraph subgraph = new Subgraph(graph);
        Graph.Node node1 = new Graph.Node("1");
        Graph.GraphNode node2 = new Graph.GraphNode("2", subgraph);
        Graph.Node node3 = new Graph.Node("3");
        Graph.Node node4 = new Graph.Node("4");

        graph.addNode(node1);
        graph.addNode(node2);
        Edge e1 = subgraph.addEdge(node3, node4);

        Edge e2 = graph.addEdge(node1, node3);

        Edge e3 = graph.addEdge(node1, node2);

        Set<Graph.Node> nodes = graph.getNodes();

        assertEquals(2, nodes.size());
        assertTrue(nodes.contains(node1));
        assertTrue(nodes.contains(node2));
        assertEquals(2, graph.getEdges().size());
        assertTrue(graph.getEdges().contains(e2));
        assertTrue(graph.getEdges().contains(e3));

        assertEquals(graph.getEdge(node1, node3), e2);
        assertEquals(graph.getEdge(node1, node2), e3);
        assertNull(graph.getEdge(node2, node1));

        Set<Graph.Node> subNodes = subgraph.getNodes();

        assertEquals(2, subNodes.size());
        assertTrue(subNodes.contains(node3));
        assertTrue(subNodes.contains(node4));
        assertEquals(1, subgraph.getEdges().size());
        assertTrue(subgraph.getEdges().contains(e1));

        assertEquals(subgraph.getEdge(node3, node4), e1);
        assertNull(subgraph.getEdge(node4, node3));
        assertNull(subgraph.getEdge(node3, node1));

    }

    @Test
    public void testCreateSubGraphNodeInGraph2() {
        Graph graph = new Graph("test", Graph.DIGRAPH);

        Subgraph subgraph = new Subgraph(graph);
        Graph.Node node1 = new Graph.Node("1");
        Graph.GraphNode node2 = new Graph.GraphNode("2", subgraph);
        Graph.Node node3 = new Graph.Node("3");
        Graph.Node node4 = new Graph.Node("4");

        graph.addNode(node1);
        graph.addNode(node2);
        Edge e1 = subgraph.addEdge(node3, node4);

        Edge e3 = graph.addEdge(node1, node2);

        Edge e2 = subgraph.addEdge(node3, node1);

        Set<Graph.Node> nodes = graph.getNodes();

        assertEquals(2, nodes.size());
        assertTrue(nodes.contains(node1));
        assertTrue(nodes.contains(node2));
        assertEquals(1, graph.getEdges().size());
        assertTrue(graph.getEdges().contains(e3));

        assertEquals(graph.getEdge(node1, node2), e3);
        assertNull(graph.getEdge(node2, node1));

        Set<Graph.Node> subNodes = subgraph.getNodes();

        assertEquals(2, subNodes.size());
        assertTrue(subNodes.contains(node3));
        assertTrue(subNodes.contains(node4));
        assertEquals(2, subgraph.getEdges().size());
        assertTrue(subgraph.getEdges().contains(e1));
        assertTrue(subgraph.getEdges().contains(e2));

        assertEquals(subgraph.getEdge(node3, node4), e1);
        assertEquals(subgraph.getEdge(node3, node1), e2);
        assertNull(subgraph.getEdge(node4, node3));

    }

    @Test
    public void testRemoveDestNode() {

        Graph graph = new Graph("test", Graph.DIGRAPH);
        Graph.Node node1 = new Graph.Node("1");
        Graph.Node node2 = new Graph.Node("2");

        graph.addEdge(node1, node2);

        graph.remove(node2);

        assertEquals(1, graph.getNodes().size());
        assertEquals(0, graph.getEdges().size());
        assertTrue(graph.getNodes().contains(node1));

    }

    @Test
    public void testRemoveSourceNode() {

        Graph graph = new Graph("test", Graph.DIGRAPH);
        Graph.Node node1 = new Graph.Node("1");
        Graph.Node node2 = new Graph.Node("2");

        graph.addEdge(node1, node2);

        graph.remove(node1);

        assertEquals(1, graph.getNodes().size());
        assertEquals(0, graph.getEdges().size());
        assertTrue(graph.getNodes().contains(node2));

    }

    @Test
    public void testRemoveEdge() {

        Graph graph = new Graph("test", Graph.DIGRAPH);
        Graph.Node node1 = new Graph.Node("1");
        Graph.Node node2 = new Graph.Node("2");

        Edge e = graph.addEdge(node1, node2);

        graph.remove(e);

        assertEquals(2, graph.getNodes().size());
        assertEquals(0, graph.getEdges().size());
        assertTrue(graph.getNodes().contains(node2));
        assertTrue(graph.getNodes().contains(node1));

    }

    static public class CustomEdge extends Graph.Edge {

    }

}
