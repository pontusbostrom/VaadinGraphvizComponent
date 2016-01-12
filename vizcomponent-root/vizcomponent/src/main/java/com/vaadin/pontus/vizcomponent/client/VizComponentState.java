package com.vaadin.pontus.vizcomponent.client;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
public class VizComponentState extends com.vaadin.shared.AbstractComponentState {

    // State can have both public variable and bean properties
    public ArrayList<Edge> graph;

    // Parameters
    public HashMap<String, String> params;

    // Node parameters
    public HashMap<String, String> nodeParams;

    // Edge parameters
    public HashMap<String, String> edgeParams;

    // graph, digraph
    public String graphType;

    public String name;

}