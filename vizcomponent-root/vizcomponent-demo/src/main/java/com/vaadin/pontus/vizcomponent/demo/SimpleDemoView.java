package com.vaadin.pontus.vizcomponent.demo;

import com.vaadin.pontus.vizcomponent.VizComponent;
import com.vaadin.pontus.vizcomponent.VizComponent.EdgeClickEvent;
import com.vaadin.pontus.vizcomponent.VizComponent.NodeClickEvent;
import com.vaadin.pontus.vizcomponent.VizComponent.NodeClickListener;
import com.vaadin.pontus.vizcomponent.model.Graph;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class SimpleDemoView extends VerticalLayout {

    public SimpleDemoView() {

        final VizComponent component = new VizComponent();
        Graph.Node node1 = new Graph.Node("n1");
        Graph.Node node2 = new Graph.Node("n2");

        Graph graph = new Graph("G", Graph.DIGRAPH);
        graph.addEdge(node1, node2);
        graph.addEdge(node2, node1);
        Graph.Edge edge1 = graph.getEdge(node1, node2);
        edge1.setParam("color", "red");
        node1.setParam("shape", "box");
        node1.setParam("label", "\"First!\"");
        edge1.setParam("label", "e1");

        component.setWidth("300px");
        component.setHeight("200px");
        component.drawGraph(graph);

        Label label = new Label(
                "In this example there are two nodes. "
                        + "The color of the nodes and edges is changed when clicking on them. "
                        + "Note also the tooltip");

        setSizeFull();
        addComponent(label);
        addComponent(component);
        setExpandRatio(component, 1);
        setComponentAlignment(component, Alignment.MIDDLE_CENTER);

        component.addClickListener(new NodeClickListener() {

            @Override
            public void nodeClicked(NodeClickEvent e) {
                Graph.Node node = e.getNode();
                component.addCss(node, "stroke", "blue");
                component.addTextCss(node, "fill", "blue");
            }

        });

        component.addClickListener(new VizComponent.EdgeClickListener() {

            @Override
            public void edgeClicked(EdgeClickEvent e) {
                component.addCss(e.getEdge(), "stroke", "blue");
                component.addTextCss(e.getEdge(), "fill", "blue");

            }

        });
    }

}
