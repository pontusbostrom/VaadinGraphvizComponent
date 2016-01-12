package com.vaadin.pontus.vizcomponent.client;

import java.util.HashMap;

public class Node {
    private String id;

    private HashMap<String, String> params;

    public Node() {
        params = new HashMap<String, String>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

}
