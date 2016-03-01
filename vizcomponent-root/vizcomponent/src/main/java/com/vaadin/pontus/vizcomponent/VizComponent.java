package com.vaadin.pontus.vizcomponent;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.vaadin.pontus.vizcomponent.client.Edge;
import com.vaadin.pontus.vizcomponent.client.Node;
import com.vaadin.pontus.vizcomponent.client.VizComponentClientRpc;
import com.vaadin.pontus.vizcomponent.client.VizComponentServerRpc;
import com.vaadin.pontus.vizcomponent.client.VizComponentState;
import com.vaadin.pontus.vizcomponent.model.Graph;
import com.vaadin.pontus.vizcomponent.model.Subgraph;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.util.ReflectTools;

/**
 * This component is used to visualize the graphs represented by
 * {@link com.vaadin.pontus.vizcomponent.model.Subgraph Graph}. It contains a
 * method for rendering graphs and then for registering ClickListeners for nodes
 * and edges in the shown graph. The internal interfaces
 * {@link VizComponent.NodeClickListener NodeClickListener} and
 * {@link VizComponent.EdgeClickListener EdgeClickListener} should be
 * implemented by event handlers. The style of the rendered graph can be
 * modified by methods to set css properties for nodes and edges. Note that the
 * graph must be rendered before these methods are called. Otherwise they will
 * have no effect. Re-rendering the graph will remove all css effects. Note that
 * resizing the graphs does not re-render it.
 *
 * @author Pontus Boström
 *
 */
@SuppressWarnings("serial")
public class VizComponent extends com.vaadin.ui.AbstractComponent {

    public interface NodeClickListener {

        public static final Method CLICK_HANDLER = ReflectTools.findMethod(
                NodeClickListener.class, "nodeClicked", NodeClickEvent.class);

        public void nodeClicked(NodeClickEvent e);
    }

    public interface EdgeClickListener {

        public static final Method CLICK_HANDLER = ReflectTools.findMethod(
                EdgeClickListener.class, "edgeClicked", EdgeClickEvent.class);

        public void edgeClicked(EdgeClickEvent e);
    }

    /**
     * Base class for Click Events
     *
     * @author Pontus Boström
     *
     */
    public static class ClickEvent extends Component.Event {

        private final MouseEventDetails details;

        public ClickEvent(Component source) {
            super(source);

            details = null;
        }

        public ClickEvent(Component source, MouseEventDetails details) {
            super(source);
            this.details = details;
        }

        public MouseEventDetails getMouseEventDetails() {
            return details;
        }
    }

    /**
     * Events emitted when nodes are clicked.
     *
     * @author Pontus Boström
     *
     */
    public static class NodeClickEvent extends ClickEvent {
        private final Subgraph.Node node;

        public NodeClickEvent(Component source) {
            super(source);
            node = null;
        }

        public NodeClickEvent(Component source, Subgraph.Node node,
                MouseEventDetails details) {
            super(source, details);
            this.node = node;
        }

        public Subgraph.Node getNode() {
            return node;
        }
    }

    /**
     * Events emitted when edges are clicked.
     *
     * @author Pontus Boström
     *
     */
    public static class EdgeClickEvent extends ClickEvent {
        private final Subgraph.Edge edge;

        public EdgeClickEvent(Component source) {
            super(source);
            edge = null;
        }

        public EdgeClickEvent(Component source, Subgraph.Edge edge,
                MouseEventDetails details) {
            super(source, details);
            this.edge = edge;
        }

        public Subgraph.Edge getEdge() {
            return edge;
        }

    }

    // To process events from the client, we implement ServerRpc
    private VizComponentServerRpc rpc = new VizComponentServerRpc() {

        @Override
        public void nodeClicked(String nodeId, MouseEventDetails mouseDetails) {
            Subgraph.Node gnode = graph.getNode(nodeId);
            if (gnode != null) {
                fireEvent(new NodeClickEvent(VizComponent.this, gnode,
                        mouseDetails));
            }
        }

        @Override
        public void edgeClicked(String edgeId, MouseEventDetails mouseDetails) {
            Subgraph.Edge gedge = graph.getEdge(edgeId);
            if (gedge != null) {
                fireEvent(new EdgeClickEvent(VizComponent.this, gedge,
                        mouseDetails));
            }
        }
    };

    private Subgraph graph;

    /**
     * The constructor creates an empty component
     */
    public VizComponent() {

        graph = null;
        // Load the Graphviz library
        new JSLoader(UI.getCurrent());
        // To receive events from the client, we register ServerRpc
        registerRpc(rpc);

    }

    /**
     * This method renders and displays the graph given as the argument
     *
     * @param graph
     *            if null then the component is emptied
     */
    public void drawGraph(Graph graph) {

        this.graph = graph;
        if (graph == null) {
            getState().graph = null;
            return;
        }
        getState().graphType = graph.getType();
        getState().graph = null;
        getState().graph = new Node();
        getState().graph.id = graph.getName();
        drawGraph(getState().graph, graph);

    }

    private void drawGraph(Node clientNode, Subgraph graph) {

        // Set the graph parameters
        HashMap<String, String> params = new HashMap<String, String>();
        for (String param : graph.getParams()) {
            params.put(param, graph.getParam(param));
        }
        clientNode.params = params;

        // Set the node parameters
        clientNode.nodeParams = null;
        params = new HashMap<String, String>();
        for (String param : graph.getNodeParams()) {
            params.put(param, graph.getNodeParam(param));
        }
        clientNode.nodeParams = params;

        // Set the edge parameters
        clientNode.edgeParams = null;
        params = new HashMap<String, String>();
        for (String param : graph.getEdgeParams()) {
            params.put(param, graph.getEdgeParam(param));
        }
        clientNode.edgeParams = params;

        // Set the graph itself
        ArrayList<Edge> newGraph = new ArrayList<Edge>();

        for (Subgraph.Node node : graph.getNodes()) {
            Node newNode = new Node();
            if (node instanceof Subgraph.GraphNode) {
                newNode.id = node.getId();
                drawGraph(newNode, ((Subgraph.GraphNode) node).getGraph());
                // The parameters of the node are ignored
            } else {
                newNode.id = node.getId();

                // Add all parameters to node
                for (String param : node.getParams()) {
                    newNode.params.put(param, node.getParam(param));
                }
            }
            // Add all edges
            Set<AbstractMap.SimpleEntry<Subgraph.Node, Subgraph.Edge>> conns = graph
                    .getConnections(node);
            if (conns.isEmpty()) {
                Edge newEdge = new Edge();
                newEdge.source = newNode;
                newGraph.add(newEdge);
            } else {
                for (AbstractMap.SimpleEntry<Subgraph.Node, Subgraph.Edge> conn : conns) {
                    Edge newEdge = new Edge();
                    newEdge.id = conn.getValue().getId();
                    newEdge.source = newNode;
                    Node destNode = new Node();
                    destNode.id = conn.getKey().getId();
                    newEdge.dest = destNode;
                    for (String param : conn.getValue().getParams()) {
                        newEdge.params.put(param,
                                conn.getValue().getParam(param));
                    }
                    newGraph.add(newEdge);
                }
            }

        }
        clientNode.graph = newGraph;

    }

    // We must override getState() to cast the state to VizComponentState
    @Override
    protected VizComponentState getState() {
        return (VizComponentState) super.getState();
    }

    public void addClickListener(NodeClickListener listener) {
        addListener(NodeClickEvent.class, listener,
                NodeClickListener.CLICK_HANDLER);
    }

    public void addClickListener(EdgeClickListener listener) {
        addListener(EdgeClickEvent.class, listener,
                EdgeClickListener.CLICK_HANDLER);
    }

    /**
     * Adds a css property with the given value to the node. The property will
     * be applied to the polygon or ellipse that makes up the node. Note that
     * the graph must be rendered before this method has any effect.
     *
     * @param node
     *            node to apply the css rule to.
     * @param property
     *            the css property.
     * @param value
     *            the css property value.
     */
    public void addCss(Subgraph.Node node, String property, String value) {
        getRpcProxy(VizComponentClientRpc.class).addNodeCss(node.getId(),
                property, value);
    }

    /**
     * Adds a css property with the given value to the node text. Note that the
     * graph must be rendered before this method has any effect.
     *
     * @param node
     *            node to apply the css rule to.
     * @param property
     *            the css property.
     * @param value
     *            the css property value.
     */
    public void addTextCss(Subgraph.Node node, String property, String value) {
        getRpcProxy(VizComponentClientRpc.class).addNodeTextCss(node.getId(),
                property, value);
    }

    /**
     * The same as for addCss for nodes, but this applies to edge heads and
     * tails as well as paths.
     *
     * @param edge
     *            edge to apply the css rule to.
     * @param property
     *            the css property.
     * @param value
     *            the css property value.
     */
    public void addCss(Subgraph.Edge edge, String property, String value) {
        getRpcProxy(VizComponentClientRpc.class).addEdgeCss(edge.getId(),
                property, value);
    }

    /**
     * The same as for addTextCss for nodes, but this applies to the edge label.
     *
     * @param edge
     *            edge to apply the css rule to.
     * @param property
     *            the css property.
     * @param value
     *            the css property value.
     */
    public void addTextCss(Subgraph.Edge edge, String property, String value) {
        getRpcProxy(VizComponentClientRpc.class).addEdgeTextCss(edge.getId(),
                property, value);
    }

}
