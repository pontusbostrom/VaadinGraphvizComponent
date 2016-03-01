package com.vaadin.pontus.vizcomponent.client;

import java.util.HashMap;

public class Edge {

    public HashMap<String, String> params;
    public Node source;
    public Node dest;
    public String id;

    public Edge() {
        params = new HashMap<String, String>();

    }
}
