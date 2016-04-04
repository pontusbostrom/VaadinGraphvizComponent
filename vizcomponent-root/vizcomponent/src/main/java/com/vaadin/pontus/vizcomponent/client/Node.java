package com.vaadin.pontus.vizcomponent.client;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
    public String id;

    public HashMap<String, String> params;

    // Inheritance in shared state appears not to work, hence also data needed
    // only in graph nodes is included here

    public ArrayList<Edge> graph;
    // Node parameters
    public HashMap<String, String> nodeParams;

    // Edge parameters
    public HashMap<String, String> edgeParams;

    public Node() {
        params = new HashMap<String, String>();
        graph = null;
        nodeParams = null;
        edgeParams = null;
    }

}
