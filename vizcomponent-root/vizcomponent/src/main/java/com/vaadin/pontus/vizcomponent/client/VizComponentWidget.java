package com.vaadin.pontus.vizcomponent.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.vectomatic.dom.svg.OMElement;
import org.vectomatic.dom.svg.OMNodeList;
import org.vectomatic.dom.svg.OMSVGLength;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class VizComponentWidget extends FlowPanel {

    private OMSVGSVGElement svg;
    private HashMap<String, String> svgIdToNodeIdMap;
    private HashMap<String, String> svgIdToEdgeIdMap;
    private HashMap<String, String> nodeIdToSvgIdMap;
    private HashMap<String, String> edgeIdToSvgIdMap;

    public VizComponentWidget() {

        // CSS class-name should not be v- prefixed
        setStyleName("vizcomponent");
        svgIdToNodeIdMap = new HashMap<String, String>();
        svgIdToEdgeIdMap = new HashMap<String, String>();
        nodeIdToSvgIdMap = new HashMap<String, String>();
        edgeIdToSvgIdMap = new HashMap<String, String>();

    }

    public void renderGraph(VizComponentState graph) {
        ArrayList<Edge> connections = graph.graph;
        if (svg != null) {
            getElement().removeChild(svg.getElement());
            svg = null;
        }
        if (connections == null || connections.isEmpty()) {
            return;
        }
        int nodeCounter = 1;
        int edgeCounter = 1;
        String svgNodeId = null;
        String svgEdgeId = null;
        String connSymbol;
        if ("graph".equals(graph.graphType)) {
            // It is undirected graph
            connSymbol = " -- ";
        } else {
            // It is a digraph
            connSymbol = " -> ";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(graph.graphType);
        builder.append(" ");
        if (graph.name != null) {
            builder.append(graph.name);
        }
        builder.append(" { ");
        if (!graph.params.isEmpty()) {
            writeParameters(graph.params, builder, ";\n");
            builder.append(";\n");
        }
        if (!graph.nodeParams.isEmpty()) {
            builder.append("node ");
            writeParameters(graph.nodeParams, builder);
            builder.append(";");
        }
        if (!graph.edgeParams.isEmpty()) {
            builder.append("edge ");
            writeParameters(graph.edgeParams, builder);
        }
        for (Edge edge : connections) {
            Node source = edge.getSource();
            // Produce a node in case there are parameters for it and it
            // hasn't been processed before
            if (!nodeIdToSvgIdMap.containsKey(source.getId())) {
                svgNodeId = "node" + nodeCounter++;
                svgIdToNodeIdMap.put(svgNodeId, source.getId());
                nodeIdToSvgIdMap.put(source.getId(), svgNodeId);
                HashMap<String, String> params = source.getParams();
                if (!params.isEmpty()) {
                    builder.append(source.getId());
                    // Produce params
                    writeParameters(params, builder);
                    builder.append(";\n");
                }
            }
            if (edge.getDest() != null) {
                // Produce an edge
                // Each edge only occurs once
                svgEdgeId = "edge" + edgeCounter++;
                svgIdToEdgeIdMap.put(svgEdgeId, edge.getId());
                edgeIdToSvgIdMap.put(edge.getId(), svgEdgeId);
                builder.append(source.getId());
                builder.append(connSymbol);
                builder.append(edge.getDest().getId());

                HashMap<String, String> params = edge.getParams();
                if (!params.isEmpty()) {
                    // Produce parameters
                    writeParameters(params, builder);
                }
                builder.append(";\n");
            }
        }

        builder.append(" } ");

        try {
            String result = compileSVG(builder.toString());
            svg = OMSVGParser.parse(result);
            svg.setWidth(OMSVGLength.SVG_LENGTHTYPE_PX, getOffsetWidth());
            svg.setHeight(OMSVGLength.SVG_LENGTHTYPE_PX, getOffsetHeight());
            getElement().appendChild(svg.getElement());

        } catch (JavaScriptException e) {
            String result = e.getDescription();
            Label label = new Label(result);
            add(label);
        }
    }

    private void writeParameters(HashMap<String, String> params,
            StringBuilder builder) {
        if (!params.isEmpty()) {
            // Produce parameters
            builder.append("[");
            writeParameters(params, builder, ",");
            builder.append("]");
        }
    }

    private void writeParameters(HashMap<String, String> params,
            StringBuilder builder, String sep) {
        // Produce parameters
        Iterator<String> it = params.keySet().iterator();
        String p = it.next();
        String v = params.get(p);
        builder.append(p);
        builder.append("=");
        builder.append(v);
        while (it.hasNext()) {
            builder.append(sep);
            p = it.next();
            v = params.get(p);
            builder.append(p);
            builder.append("=");
            builder.append(v);
        }
    }

    private static native String compileSVG(String graph)
    /*-{
          var result = $wnd.Viz(graph, { format: "svg" });
          return result;
        }-*/;

    public void addNodeClickHandler(ClickHandler handler) {
        if (svg == null) {
            return;
        }
        for (String nodeId : svgIdToNodeIdMap.keySet()) {
            OMElement svgNode = svg.getElementById(nodeId);
            svgNode.addDomHandler(handler, ClickEvent.getType());
        }
    }

    public void addEdgeClickHandler(ClickHandler handler) {
        if (svg == null) {
            return;
        }
        for (String edgeId : svgIdToEdgeIdMap.keySet()) {
            OMElement svgNode = svg.getElementById(edgeId);
            svgNode.addDomHandler(handler, ClickEvent.getType());
        }
    }

    public String getNodeId(Element e) {
        String id = e.getAttribute("id");
        return svgIdToNodeIdMap.get(id);
    }

    public String getEdgeId(Element e) {
        String id = e.getAttribute("id");
        return svgIdToEdgeIdMap.get(id);
    }

    public void addNodeCss(String nodeId, String property, String value) {
        if (svg != null) {
            // Style the polygon or ellipse that make up the node
            // In case some other shape then nothing happens
            String id = nodeIdToSvgIdMap.get(nodeId);
            OMElement svgNode = svg.getElementById(id);
            applyCssToElement(svgNode, property, value);
        }
    }

    private void applyCssToElement(OMElement svgNode, String property,
            String value) {
        OMNodeList<OMElement> children = svgNode
                .getElementsByTagName("polygon");
        if (children.getLength() > 0) {
            for (OMElement child : children) {
                child.getElement().getStyle().setProperty(property, value);
            }
        } else {
            children = svgNode.getElementsByTagName("ellipse");
            for (OMElement child : children) {
                child.getElement().getStyle().setProperty(property, value);
            }
        }
    }

    public void addNodeTextCss(String nodeId, String property, String value) {
        if (svg != null) {
            // Style the text in node
            String id = nodeIdToSvgIdMap.get(nodeId);
            OMElement svgNode = svg.getElementById(id);
            applyTextCssToElement(svgNode, property, value);
        }
    }

    public void addEdgeTextCss(String edgeId, String property, String value) {
        if (svg != null) {
            // Style the text in node
            String id = edgeIdToSvgIdMap.get(edgeId);
            OMElement svgNode = svg.getElementById(id);
            applyTextCssToElement(svgNode, property, value);
        }
    }

    private void applyTextCssToElement(OMElement svgNode, String property,
            String value) {
        OMNodeList<OMElement> children = svgNode.getElementsByTagName("text");
        for (OMElement child : children) {
            child.getElement().getStyle().setProperty(property, value);
        }
    }

    public void addEdgeCss(String edgeId, String property, String value) {
        if (svg != null) {
            // Style the path and polygon that make up the node
            String id = edgeIdToSvgIdMap.get(edgeId);
            OMElement svgNode = svg.getElementById(id);
            applyCssToElement(svgNode, property, value);

            OMNodeList<OMElement> children = svgNode
                    .getElementsByTagName("path");
            for (OMElement child : children) {
                child.getElement().getStyle().setProperty(property, value);
            }

        }
    }

    public void updateSvgSize() {
        if (svg == null) {
            return;
        }
        svg.setWidth(OMSVGLength.SVG_LENGTHTYPE_PX, getOffsetWidth());
        svg.setHeight(OMSVGLength.SVG_LENGTHTYPE_PX, getOffsetHeight());
    }

}