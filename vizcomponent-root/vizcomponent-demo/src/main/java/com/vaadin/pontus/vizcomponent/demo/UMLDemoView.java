package com.vaadin.pontus.vizcomponent.demo;

import java.util.Arrays;
import java.util.List;

import com.vaadin.pontus.vizcomponent.VizComponent;
import com.vaadin.pontus.vizcomponent.VizComponent.NodeClickEvent;
import com.vaadin.pontus.vizcomponent.VizComponent.NodeClickListener;
import com.vaadin.pontus.vizcomponent.model.Graph;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class UMLDemoView extends VerticalLayout {

    private VizComponent component;
    private int width = 800;
    private int height = 800;
    private Graph.Node selected = null;
    private Label classInfo;
    private String graphName;

    public UMLDemoView() {

        component = new VizComponent();

        graphName = "\"Class diagram\"";
        Label label = new Label(
                "This example show an (simplified) UML diagram of a (tiny) part of the Vaadin framework. "
                        + "Nodes with HTML content is used for the classes. "
                        + "When a class is clicked more detailed (here dummy) information is shown at the right of the diagram");

        HorizontalLayout layout = new HorizontalLayout();
        Panel panel = new Panel("Graph " + graphName);
        panel.setHeight("600px");
        panel.setWidth("800px");
        panel.setContent(component);
        layout.addComponent(panel);

        classInfo = new Label("<h1> Class info </h1>", ContentMode.HTML);
        layout.addComponent(classInfo);

        layout.setSpacing(true);

        layout.setComponentAlignment(panel, Alignment.TOP_LEFT);
        layout.setComponentAlignment(classInfo, Alignment.TOP_LEFT);

        addComponent(label);
        addComponent(layout);

        setMargin(true);
        setSpacing(true);

        createAndRenderGraph(width, height);

    }

    public void createAndRenderGraph(int width, int height) {
        component.setWidth(width + "px");
        component.setHeight(height + "px");
        Graph graph = new Graph(graphName, Graph.DIGRAPH);
        graph.setNodeParameter("shape", "none");
        graph.setParam("rankdir", "BT");

        Graph.Node node1 = new Graph.Node("java.lang.Object");
        node1.setParam(
                "label",
                createLabel(
                        "Object",
                        Arrays.asList(
                                "<font color=\"red\">int</font>  hashCode()",
                                "<font color=\"red\">boolean</font>  equals(Object o)"),
                        false));
        graph.addNode(node1);

        Graph.Node node2 = new Graph.Node(
                "com.vaadin.server.AbstractClientConnector");
        node2.setParam(
                "label",
                createLabel(
                        "AbstractClientConnector",
                        Arrays.asList(
                                "<font color=\"red\">void</font>  addExtension(Extension extension)",
                                "<font color=\"red\">void</font>  attach()"),
                        false));
        graph.addNode(node2);

        Graph.Node node3 = new Graph.Node("com.vaadin.ui.AbstractComponent");
        node3.setParam(
                "label",
                createLabel(
                        "AbstractComponent",
                        Arrays.asList(
                                "<font color=\"red\">void</font>  addStyleName(String style) ",
                                "<font color=\"red\">void</font>  focus()"),
                        false));
        graph.addNode(node3);

        Graph.Node node4 = new Graph.Node(
                "com.vaadin.ui.AbstractComponentContainer");
        node4.setParam(
                "label",
                createLabel(
                        "AbstractComponentContainer",
                        Arrays.asList(
                                "<font color=\"red\">void</font>  addComponent(Component c)  ",
                                "<font color=\"red\">void</font>  addComponents(Component... components) "),
                        false));
        graph.addNode(node4);

        Graph.Node node5 = new Graph.Node("com.vaadin.ui.Component");
        node5.setParam(
                "label",
                createLabel(
                        "Component",
                        Arrays.asList(
                                "<font color=\"red\">void</font>  addStyleName(String style) ",
                                "String getId()"), true));
        graph.addNode(node5);

        Graph.Node node6 = new Graph.Node("com.vaadin.ui.HasComponents");
        node6.setParam(
                "label",
                createLabel("HasComponents",
                        Arrays.asList("Iterator&lt;Component&gt;  iterator()"),
                        true));
        graph.addNode(node6);

        addInhertitanceEdge(graph, node2, node1, null);
        addInhertitanceEdge(graph, node3, node2, null);
        addInhertitanceEdge(graph, node4, node3, null);

        addInhertitanceEdge(graph, node6, node5, null);

        addInhertitanceEdge(graph, node3, node5, "implements");
        addInhertitanceEdge(graph, node4, node6, "implements");

        addEdge(graph, node3, node6, "parent");
        addEdge(graph, node4, node5, "components");

        component.addClickListener(new NodeClickListener() {

            @Override
            public void nodeClicked(NodeClickEvent e) {
                if (selected != null) {
                    component.addCss(selected, "stroke", "black");
                }
                selected = e.getNode();
                component.addCss(selected, "stroke", "blue");
                classInfo.setValue(getClassInfo(selected));

            }

        });
        component.drawGraph(graph);
    }

    private String createLabel(String clazz, List<String> methods,
            boolean isInterface) {
        StringBuilder builder = new StringBuilder();
        builder.append("<<table bgcolor=\"#faee66\">");
        if (isInterface) {
            builder.append("<tr><td border=\"0\">");
            builder.append("&lt;&lt;interface&gt;&gt;");
            builder.append("</td></tr>");
        }
        builder.append("<tr><td sides=\"b\">");
        builder.append("<b>");
        builder.append(clazz);
        builder.append("</b>");
        builder.append("</td></tr>");
        for (String method : methods) {
            builder.append("<tr><td align=\"left\" border=\"0\">");
            builder.append(method);
            builder.append("</td></tr>");
        }
        builder.append("</table>>");
        return builder.toString();
    }

    private void addInhertitanceEdge(Graph graph, Graph.Node from,
            Graph.Node to, String label) {
        addEdge(graph, from, to, label);
        Graph.Edge edge = graph.getEdge(from, to);
        edge.setParam("arrowhead", "empty");
    }

    private void addEdge(Graph graph, Graph.Node from, Graph.Node to,
            String label) {
        graph.addEdge(from, to);
        Graph.Edge edge = graph.getEdge(from, to);
        if (label != null) {
            edge.setParam("label", label);

        }
    }

    private String getClassInfo(Graph.Node node) {
        // Generate some text
        StringBuilder builder = new StringBuilder();
        builder.append("<h1>");
        builder.append(node.getId());
        builder.append("</h1>");
        builder.append("<p>");
        for (int i = 0; i < 20; i++) {
            builder.append(node.getId());
            builder.append("<br> ");
        }
        builder.append("</p>");
        return builder.toString();
    }

}
