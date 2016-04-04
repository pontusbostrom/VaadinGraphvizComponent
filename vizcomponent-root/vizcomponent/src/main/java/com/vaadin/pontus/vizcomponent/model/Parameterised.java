package com.vaadin.pontus.vizcomponent.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
