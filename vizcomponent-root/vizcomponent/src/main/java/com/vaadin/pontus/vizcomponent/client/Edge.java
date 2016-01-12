package com.vaadin.pontus.vizcomponent.client;

import java.util.HashMap;

public class Edge {

    private HashMap<String, String> params;
    private Node source;
    private Node dest;
    private String id;

    public Edge() {
        params = new HashMap<String, String>();

    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getDest() {
        return dest;
    }

    public void setDest(Node dest) {
        this.dest = dest;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
