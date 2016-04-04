package com.vaadin.pontus.vizcomponent.model;

public class Graph extends Subgraph {

    public static final String DIGRAPH = "digraph";
    public static final String GRAPH = "graph";

    private String type;
    private String id;

    public Graph(String name, String type) {
        this.type = type;
        id = GraphElement.deescapeId(name);
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return id;
    }

}
