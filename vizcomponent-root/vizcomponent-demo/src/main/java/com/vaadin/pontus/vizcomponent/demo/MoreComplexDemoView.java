package com.vaadin.pontus.vizcomponent.demo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.pontus.vizcomponent.VizComponent;
import com.vaadin.pontus.vizcomponent.VizComponent.EdgeClickEvent;
import com.vaadin.pontus.vizcomponent.VizComponent.NodeClickEvent;
import com.vaadin.pontus.vizcomponent.VizComponent.NodeClickListener;
import com.vaadin.pontus.vizcomponent.client.ZoomSettings;
import com.vaadin.pontus.vizcomponent.model.Graph;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class MoreComplexDemoView extends VerticalLayout {

    private VizComponent component;
    private int width = 1000;
    private int height = 1000;
    private int noEdges = 0;

    public MoreComplexDemoView() {

        component = new VizComponent();

        Label label = new Label(
                "In this example there are 100 nodes with a selectable number of "
                        + "random connections between them. "
                        + "The layout direction and type of graph (directed/undirected) can be changed. "
                        + "The color of the nodes is changed when clicking on them. "
                        + "Note that just changing the size of the graph does not re-render it.");

        setSizeFull();
        Panel panel = new Panel("Graph G");
        panel.setHeight("600px");
        panel.setWidth("800px");
        panel.setContent(component);

        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.setMargin(true);
        hLayout.setSpacing(true);
        hLayout.setSizeFull();
        VerticalLayout paramLayout = new VerticalLayout();
        final TextField noEdgesBox = new TextField("Number of edges");
        final TextField directionBox = new TextField(
                "Layout direction (TB, LR, BT, RL)");
        final RadioButtonGroup<String> typeOption = new RadioButtonGroup<>(
                "Graph type");
        typeOption.setItems("Directed", "Undirected");
        typeOption.setValue("Directed");
        final Button drawButton = new Button("Draw graph");
        final TextField widthBox = new TextField("Graph width (px)");
        widthBox.setValue(Integer.toString(width));
        final TextField heightBox = new TextField("Graph width (px)");
        heightBox.setValue(Integer.toString(height));
        final Button resizeButton = new Button("Resize");
        final Button size100Button = new Button("Size to 100%");

        paramLayout.addComponent(noEdgesBox);
        paramLayout.addComponent(directionBox);
        paramLayout.addComponent(typeOption);
        paramLayout.addComponent(drawButton);
        paramLayout.addComponent(widthBox);
        paramLayout.addComponent(heightBox);
        paramLayout.addComponent(resizeButton);
        paramLayout.addComponent(size100Button);
        paramLayout.setSpacing(true);
        paramLayout.setWidth(300, Unit.PIXELS);

        hLayout.addComponent(paramLayout);
        hLayout.addComponent(panel);
        hLayout.setExpandRatio(panel, 1);

        addComponent(label);
        addComponent(hLayout);
        setExpandRatio(hLayout, 1);

        // setComponentAlignment(panel, Alignment.MIDDLE_CENTER);

        drawButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                try {
                    noEdges = Integer.parseInt(noEdgesBox.getValue());
                } catch (NumberFormatException e) {
                    Notification.show("Number of edges not an integer",
                            Type.ERROR_MESSAGE);
                    return;
                }
                String direction = directionBox.getValue();
                if (!"TB".equals(direction) && !"LR".equals(direction)
                        && !"BT".equals(direction) && !"RL".equals(direction)) {
                    direction = "TB";
                }
                String typeStr = typeOption.getValue();
                String type = Graph.GRAPH;
                if (typeStr.equals("Directed")) {
                    type = Graph.DIGRAPH;
                } else if (typeStr.equals("Undirected")) {
                    type = Graph.GRAPH;
                }
                if (setWidthAndHeight(heightBox.getValue(), widthBox.getValue())) {
                    // Only redraw if all parameters ok
                    createAndRenderGraph(noEdges, direction, type, width,
                            height);
                }
            }

        });

        resizeButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (setWidthAndHeight(heightBox.getValue(), widthBox.getValue())) {
                    component.setWidth(width + "px");
                    component.setHeight(height + "px");
                }
            }

        });

        size100Button.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (setWidthAndHeight(heightBox.getValue(), widthBox.getValue())) {
                    component.setWidth(100, Unit.PERCENTAGE);
                    component.setHeight(100, Unit.PERCENTAGE);
                }
            }

        });

        component.addClickListener(new NodeClickListener() {

            @Override
            public void nodeClicked(NodeClickEvent e) {
                Graph.Node node = e.getNode();
                component.addCss(node, "stroke", "blue");
            }

        });

        component.addClickListener(new VizComponent.EdgeClickListener() {

            @Override
            public void edgeClicked(EdgeClickEvent e) {
                System.out.println(e.getEdge());

            }

        });
    }

    public void createAndRenderGraph(int noEdges, String direction,
            String type, int width, int height) {

        ZoomSettings zs = new ZoomSettings();
        zs.setControlIconsEnabled(true); // TODO: make UI in DEMO for zoom & pan
                                         // settings
        zs.setPreventMouseEventsDefault(true);
        component.setPanZoomSettings(zs);
        component.setWidth(width + "px");
        component.setHeight(height + "px");
        Graph graph = new Graph("G", type);
        graph.setParam("rankdir", direction);
        graph.setParam("fontsize", "30");

        // Create a 100 nodes
        List<Graph.Node> nodes = new ArrayList<Graph.Node>();
        for (int i = 1; i <= 100; i++) {
            Graph.Node node = new Graph.Node("n" + i);
            node.setParam("label", Integer.toString(i));
            nodes.add(node);
            graph.addNode(node);
        }

        Set<Graph.Node> usedNodes = new HashSet<Graph.Node>();
        for (int i = 0; i < noEdges; i++) {
            int source = (int) (Math.random() * 100);
            int dest = (int) (Math.random() * 100);
            graph.addEdge(nodes.get(source), nodes.get(dest));
            usedNodes.add(nodes.get(source));
            usedNodes.add(nodes.get(dest));
        }
        component.drawGraph(graph);

    }

    private boolean setWidthAndHeight(String sHeight, String sWidth) {
        int twidth = 0;
        int theight = 0;

        try {
            twidth = Integer.parseInt(sWidth);
        } catch (NumberFormatException e) {
            Notification.show("Width is not an integer", Type.ERROR_MESSAGE);
            return false;
        }

        try {
            theight = Integer.parseInt(sHeight);
        } catch (NumberFormatException e) {
            Notification.show("Height is not an integer", Type.ERROR_MESSAGE);
            return false;
        }

        width = twidth;
        height = theight;
        return true;

    }

}
