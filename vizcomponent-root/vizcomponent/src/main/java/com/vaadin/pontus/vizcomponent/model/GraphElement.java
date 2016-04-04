package com.vaadin.pontus.vizcomponent.model;

/**
 * Base class for nodes and edges.
 *
 * @author Pontus Boström
 *
 */
public abstract class GraphElement extends Parameterised {
    protected String id;

    public GraphElement() {
        id = "";
    }

    public GraphElement(String id) {
        this.id = deescapeId(id);
    }

    public static String deescapeId(String str) {
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        } else {
            return str;
        }
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
