package com.vaadin.pontus.vizcomponent.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Widget to display a graph represented by
 * {@link com.vaadin.pontus.vizcomponent.client.VizComponentState
 * VizComponentState}
 *
 * The graph is rendered into an SVG element. When the graph has been rendered,
 * event handlers can be added to nodes and edges, and nodes and edges can be
 * styled using CSS.
 *
 * @author Pontus Boström
 *
 */
public class VizComponentWidget extends FlowPanel {

    private Element svg;
    private HashMap<String, String> svgIdToNodeIdMap;
    private HashMap<String, String> svgIdToEdgeIdMap;
    private HashMap<String, String> nodeIdToSvgIdMap;
    private HashMap<String, String> edgeIdToSvgIdMap;

    static int globalComponentID = 1;
    private final int componentID;
    private JavaScriptObject zoomPanHandler;
    private int nodeCounter;
    private int edgeCounter;

    public VizComponentWidget() {

        // CSS class-name should not be v- prefixed
        setStyleName("vizcomponent");
        svgIdToNodeIdMap = new HashMap<String, String>();
        svgIdToEdgeIdMap = new HashMap<String, String>();
        nodeIdToSvgIdMap = new HashMap<String, String>();
        edgeIdToSvgIdMap = new HashMap<String, String>();
        componentID = globalComponentID++;
        nodeCounter = 0;
        edgeCounter = 0;

    }

    public void renderGraph(Node graph, String type,
            final ZoomSettings zoomSettings) {

        svgIdToNodeIdMap.clear();
        svgIdToEdgeIdMap.clear();
        nodeIdToSvgIdMap.clear();
        edgeIdToSvgIdMap.clear();
        nodeCounter = 1;
        edgeCounter = 1;
        if (svg != null) {
            getElement().removeChild(svg);
            svg = null;
        }
        if (graph == null || graph.graph == null) {
            return;
        }
        if (graph.graph.isEmpty()) {
            return;
        }
        String connSymbol;
        if ("graph".equals(type)) {
            // It is undirected graph
            connSymbol = " -- ";
        } else {
            // It is a digraph
            connSymbol = " -> ";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(type);
        builder.append(" ");
        if (graph.id != null) {
            builder.append(graph.id);
        }

        renderGraph(graph, connSymbol, builder);

        try {
            String result = compileSVG(builder.toString());
            getElement().setInnerHTML(result);
            svg = getElement().getFirstChildElement();
            final String boxid = "_svgbox" + componentID;
            svg = getElement().getFirstChildElement();
            svg.setAttribute("width", "100%");
            svg.setAttribute("height", "100%");
            svg.setId(boxid);
            if (zoomSettings != null) {
                // For some reason zooming doesn't work when the component is
                // created
                // This way zoom actions are deferred until afterwards.
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        zoomPanHandler = setupZoomPanHandler(boxid,
                                zoomSettings);
                    }
                });
            }

        } catch (JavaScriptException e) {
            String result = e.getDescription();
            Label label = new Label(result);
            add(label);
        }
    }

    private void renderGraph(Node graph, String connSymbol,
            StringBuilder builder) {
        ArrayList<Edge> connections = graph.graph;
        // connections should not be empty

        String svgNodeId = null;
        String svgEdgeId = null;

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
            Node source = edge.source;
            if (source.graph != null) {
                builder.append("subgraph ");
                builder.append(source.id);
                renderGraph(source, connSymbol, builder);

            } else {
                // Produce a node in case there are parameters for it and it
                // hasn't been processed before
                String sourceId = deescapeId(source.id);
                if (!nodeIdToSvgIdMap.containsKey(sourceId)) {
                    svgNodeId = "node" + nodeCounter++;
                    svgIdToNodeIdMap.put(svgNodeId, sourceId);
                    nodeIdToSvgIdMap.put(sourceId, svgNodeId);
                    // TODO: The below is redundant. This would be an edge
                    // statement with empty dest
                    HashMap<String, String> params = source.params;
                    builder.append(source.id);
                    // Produce params
                    params.put("id", svgNodeId); // Use this ID for GraphViz
                    // also
                    if (!params.isEmpty()) {
                        writeParameters(params, builder);
                    }
                    builder.append(";\n");
                }
            }
            if (edge.dest != null) {
                // Produce an edge
                // Each edge only occurs once
                String edgeId = deescapeId(edge.id);
                svgEdgeId = "edge" + edgeCounter++;
                svgIdToEdgeIdMap.put(svgEdgeId, edgeId);
                edgeIdToSvgIdMap.put(edgeId, svgEdgeId);
                if (source.graph != null) {
                    builder.append("subgraph ");
                    builder.append(source.id);
                    renderGraph(source, connSymbol, builder);
                } else {
                    builder.append(source.id);
                }
                builder.append(connSymbol);
                if (edge.dest.graph != null) {
                    builder.append("subgraph ");
                    builder.append(edge.dest.id);
                    renderGraph(edge.dest, connSymbol, builder);
                } else {
                    builder.append(edge.dest.id);
                }
                HashMap<String, String> params = edge.params;
                params.put("id", svgEdgeId); // Use this ID for GraphViz also
                // Produce parameters
                if (!params.isEmpty()) {
                    writeParameters(params, builder);
                }
                builder.append(";\n");
            }

        }

        builder.append(" } ");

    }

    private String deescapeId(String str) {
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        } else {
            return str;
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

    private static native JavaScriptObject setupZoomPanHandler(String id,
            ZoomSettings zoomsettings)
    /*-{
          return $wnd.svgPanZoom('#' + id, {
    		  panEnabled: zoomsettings.@com.vaadin.pontus.vizcomponent.client.ZoomSettings::panEnabled
    		, controlIconsEnabled: zoomsettings.@com.vaadin.pontus.vizcomponent.client.ZoomSettings::controlIconsEnabled
    		, zoomEnabled: zoomsettings.@com.vaadin.pontus.vizcomponent.client.ZoomSettings::zoomEnabled
    		, dblClickZoomEnabled: zoomsettings.@com.vaadin.pontus.vizcomponent.client.ZoomSettings::dblClickZoomEnabled
    		, mouseWheelZoomEnabled: zoomsettings.@com.vaadin.pontus.vizcomponent.client.ZoomSettings::mouseWheelZoomEnabled
    		, preventMouseEventsDefault: zoomsettings.@com.vaadin.pontus.vizcomponent.client.ZoomSettings::preventMouseEventsDefault
    		, zoomScaleSensitivity: zoomsettings.@com.vaadin.pontus.vizcomponent.client.ZoomSettings::zoomScaleSensitivity
    		, minZoom: zoomsettings.@com.vaadin.pontus.vizcomponent.client.ZoomSettings::minZoom
    		, maxZoom: zoomsettings.@com.vaadin.pontus.vizcomponent.client.ZoomSettings::maxZoom
    		, fit: zoomsettings.@com.vaadin.pontus.vizcomponent.client.ZoomSettings::fit
    		, contain: zoomsettings.@com.vaadin.pontus.vizcomponent.client.ZoomSettings::contain
    		, center: zoomsettings.@com.vaadin.pontus.vizcomponent.client.ZoomSettings::center
    		, refreshRate: 'auto'
    		});
        }-*/
    ;

    private static native String compileSVG(String graph)
    /*-{
          var result = $wnd.Viz(graph, { format: "svg" });
          return result;
        }-*/;

    private static native void panToElement(JavaScriptObject zoomPanHandler,
            Element el)
    /*-{
    		zoomPanHandler.pan({x:0,y:0});

    		var bbox = el.getBBox();
    		var cx = bbox.x + bbox.width/2;
    		var cy = bbox.y + bbox.height/2;

    		var sizes = zoomPanHandler.getSizes();
    		cy += sizes.viewBox.height;

    		var realZoom = zoomPanHandler.getSizes().realZoom;
    		var width = sizes.width;
    		var height = sizes.height;
    		var panx = -(cx * realZoom)+(width/2);
    		var pany = -(cy * realZoom)+(height/2);
    		zoomPanHandler.pan({
    			x: panx,
       			y: pany
    		});

        }-*/;

    private static native void fit(JavaScriptObject zoomPanHandler)
    /*-{
          zoomPanHandler.fit();
          zoomPanHandler.center();
        }-*/;

    private static native void center(JavaScriptObject zoomPanHandler)
    /*-{
          zoomPanHandler.center();
        }-*/;

    public void addNodeClickHandler(final VizClickHandler handler) {
        if (svg == null) {
            return;
        }
        addClickHandler(svgIdToNodeIdMap.keySet(), handler);
    }

    private void addClickHandler(Set<String> ids, final VizClickHandler handler) {
        for (String nodeId : ids) {
            Element svgNode = DOM.getElementById(nodeId);
            Event.sinkEvents(svgNode, Event.ONCLICK);
            Event.setEventListener(svgNode, new EventListener() {

                @Override
                public void onBrowserEvent(Event event) {
                    handler.onClick(event);
                }
            });
        }
    }

    public void addEdgeClickHandler(final VizClickHandler handler) {
        if (svg == null) {
            return;
        }
        addClickHandler(svgIdToEdgeIdMap.keySet(), handler);
    }

    public String getNodeId(Element e) {
        String id = e.getAttribute("id");
        return svgIdToNodeIdMap.get(id);
    }

    public String getEdgeId(Element e) {
        String id = e.getAttribute("id");
        return svgIdToEdgeIdMap.get(id);
    }

    public void centerToNode(String nodeId) {
        if (svg != null) {
            String id = nodeIdToSvgIdMap.get(nodeId);
            panToElement(zoomPanHandler, DOM.getElementById(id));
        }
    }

    public void centerGraph() {
        center(zoomPanHandler);
    }

    public void fitGraph() {
        fit(zoomPanHandler);
    }

    public void addNodeCss(String nodeId, String property, String value) {
        if (svg != null) {
            // Style the polygon or ellipse that make up the node
            // In case some other shape then nothing happens
            String id = nodeIdToSvgIdMap.get(nodeId);
            Element svgNode = DOM.getElementById(id);
            applyCssToElement(svgNode, property, value);
        }
    }

    private void applyCssToElement(Element svgNode, String property,
            String value) {
        NodeList<Element> children = svgNode.getElementsByTagName("polygon");
        if (children.getLength() > 0) {
            for (int i = 0; i < children.getLength(); i++) {
                Element child = children.getItem(i);
                child.getStyle().setProperty(property, value);
            }
        } else {
            children = svgNode.getElementsByTagName("ellipse");
            for (int i = 0; i < children.getLength(); i++) {
                Element child = children.getItem(i);
                child.getStyle().setProperty(property, value);
            }
        }
    }

    public void addNodeTextCss(String nodeId, String property, String value) {
        if (svg != null) {
            // Style the text in node
            String id = nodeIdToSvgIdMap.get(nodeId);
            Element svgNode = DOM.getElementById(id);
            applyTextCssToElement(svgNode, property, value);
        }
    }

    public void addEdgeTextCss(String edgeId, String property, String value) {
        if (svg != null) {
            // Style the text in node
            String id = edgeIdToSvgIdMap.get(edgeId);
            Element svgNode = DOM.getElementById(id);
            applyTextCssToElement(svgNode, property, value);
        }
    }

    private void applyTextCssToElement(Element svgNode, String property,
            String value) {
        NodeList<Element> children = svgNode.getElementsByTagName("text");
        for (int i = 0; i < children.getLength(); i++) {
            Element child = children.getItem(i);
            child.getStyle().setProperty(property, value);
        }
    }

    public void addEdgeCss(String edgeId, String property, String value) {
        if (svg != null) {
            // Style the path and polygon that make up the node
            String id = edgeIdToSvgIdMap.get(edgeId);
            Element svgNode = DOM.getElementById(id);
            applyCssToElement(svgNode, property, value);

            NodeList<Element> children = svgNode.getElementsByTagName("path");
            for (int i = 0; i < children.getLength(); i++) {
                Element child = children.getItem(i);
                child.getStyle().setProperty(property, value);
            }
        }
    }

}