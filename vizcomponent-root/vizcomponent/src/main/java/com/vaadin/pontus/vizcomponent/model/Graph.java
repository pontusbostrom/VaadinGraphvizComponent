package com.vaadin.pontus.vizcomponent.model;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph extends Parameterised {
    public static abstract class GraphElement extends Parameterised {
        protected String id;

        public GraphElement() {
            id = null;
        }

        public String getId() {
            return id;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o instanceof GraphElement) {
                if (id.equals(((GraphElement) o).getId())) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class Node extends GraphElement {

        public Node(String id) {
            super();
            this.id = id;
        }
    }

    public static class Edge extends GraphElement {
        private static volatile long counter = 0L;
        private Node dest;

        public Edge() {
            super();
            id = "edge" + counter++;
        }

        public Node getDest() {
            return dest;
        }

        public void setDest(Node dest) {
            this.dest = dest;
        }

    }

    public static final String DIGRAPH = "digraph";
    public static final String GRAPH = "graph";

    final private Map<Node, Set<AbstractMap.SimpleEntry<Node, Edge>>> graph;
    private String name;
    private String type;
    private Parameterised nodeParams;
    private Parameterised edgeParams;
    private Map<String, Node> nodeMap;
    private Map<String, Edge> edgeMap;

    public Graph(String name, String type) {
        super();
        graph = new HashMap<Node, Set<AbstractMap.SimpleEntry<Node, Edge>>>();
        this.name = name;
        this.type = type;
        nodeParams = new Parameterised();
        edgeParams = new Parameterised();
        nodeMap = new HashMap<String, Node>();
        edgeMap = new HashMap<String, Edge>();
    }

    public void addNode(Node node) {
        nodeMap.put(node.getId(), node);
        graph.put(node, new HashSet<AbstractMap.SimpleEntry<Node, Edge>>());
    }

    public void addEdge(Node source, Node dest) {
        Edge edge = new Edge();
        edgeMap.put(edge.getId(), edge);
        AbstractMap.SimpleEntry<Node, Edge> edgeDest = new AbstractMap.SimpleEntry<Node, Edge>(
                dest, edge);
        if (!graph.containsKey(dest)) {
            nodeMap.put(dest.getId(), dest);
            graph.put(dest, new HashSet<AbstractMap.SimpleEntry<Node, Edge>>());
        }
        Set<AbstractMap.SimpleEntry<Node, Edge>> destSet = graph.get(source);
        if (destSet == null) {
            nodeMap.put(source.getId(), source);
            destSet = new HashSet<AbstractMap.SimpleEntry<Node, Edge>>();
            destSet.add(edgeDest);
            graph.put(source, destSet);
        } else {
            destSet.add(edgeDest);
        }
    }

    public Set<Node> getNodes() {
        return graph.keySet();
    }

    public Set<AbstractMap.SimpleEntry<Node, Edge>> getConnections(Node node) {
        return graph.get(node);
    }

    public Edge getEdge(Node source, Node dest) {
        Set<AbstractMap.SimpleEntry<Node, Edge>> destSet = graph.get(source);
        if (destSet == null || destSet.isEmpty()) {
            return null;
        }
        for (AbstractMap.SimpleEntry<Node, Edge> pair : destSet) {
            if (pair.getKey().equals(dest)) {
                return pair.getValue();
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Set<Edge> getEdges() {
        Set<Edge> edges = new HashSet<Edge>();
        for (String id : edgeMap.keySet()) {
            edges.add(edgeMap.get(id));
        }
        return edges;
    }

    public void setNodeParameter(String name, String value) {
        nodeParams.setParam(name, value);
    }

    public String getNodeParam(String name) {
        return nodeParams.getParam(name);
    }

    public Set<String> getNodeParams() {
        return nodeParams.getParams();
    }

    public Node getNode(String nodeId) {
        return nodeMap.get(nodeId);
    }

    public Edge getEdge(String edgeId) {
        return edgeMap.get(edgeId);
    }

    public void setEdgeParameter(String name, String value) {
        edgeParams.setParam(name, value);
    }

    public String getEdgeParam(String name) {
        return edgeParams.getParam(name);
    }

    public Set<String> getEdgeParams() {
        return edgeParams.getParams();
    }

}

class Parameterised {
    private final Map<String, String> params;

    public Parameterised() {
        params = new HashMap<String, String>();
    }

    public void setParam(String name, String value) {
        params.put(name, value);
    }

    public String getParam(String name) {
        return params.get(name);
    }

    public Set<String> getParams() {
        return params.keySet();
    }

}
