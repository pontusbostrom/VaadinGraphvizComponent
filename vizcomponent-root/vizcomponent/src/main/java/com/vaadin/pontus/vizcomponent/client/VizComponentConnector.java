package com.vaadin.pontus.vizcomponent.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.pontus.vizcomponent.VizComponent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.Connect;

// Connector binds client-side widget class to server-side component class
// Connector lives in the client and the @Connect annotation specifies the
// corresponding server-side component
@SuppressWarnings("serial")
@Connect(VizComponent.class)
public class VizComponentConnector extends AbstractComponentConnector {

    // ServerRpc is used to send events to server. Communication implementation
    // is automatically created here
    VizComponentServerRpc rpc = RpcProxy.create(VizComponentServerRpc.class,
            this);

    public VizComponentConnector() {

        // To receive RPC events from server, we register ClientRpc
        // implementation
        registerRpc(VizComponentClientRpc.class, new VizComponentClientRpc() {

            @Override
            public void fitGraph() {
                getWidget().fitGraph();
            }

            @Override
            public void centerGraph() {
                getWidget().centerGraph();
            }

            @Override
            public void centerToNode(String nodeId) {
                getWidget().centerToNode(nodeId);
            }

            @Override
            public void addNodeCss(String nodeId, String property, String value) {
                getWidget().addNodeCss(nodeId, property, value);

            }

            @Override
            public void addEdgeCss(String edgeId, String property, String value) {
                getWidget().addEdgeCss(edgeId, property, value);

            }

            @Override
            public void addNodeTextCss(String nodeId, String property,
                    String value) {
                getWidget().addNodeTextCss(nodeId, property, value);

            }

            @Override
            public void addEdgeTextCss(String edgeId, String property,
                    String value) {
                getWidget().addEdgeTextCss(edgeId, property, value);

            }
        });

    }

    class NodeClickHandler implements VizClickHandler {
        @Override
        public void onClick(NativeEvent event) {
            Element e = Element.as(event.getEventTarget());
            String nodeId = getWidget().getNodeId(e.getParentElement());

            MouseEventDetails details = MouseEventDetailsBuilder
                    .buildMouseEventDetails(event, getWidget().getElement());
            rpc.nodeClicked(nodeId, details);

            event.stopPropagation();
            event.preventDefault();
        }
    }

    class EdgeClickHandler implements VizClickHandler {
        @Override
        public void onClick(NativeEvent event) {
            Element e = Element.as(event.getEventTarget());
            String edgeId = getWidget().getEdgeId(e.getParentElement());
            MouseEventDetails details = MouseEventDetailsBuilder
                    .buildMouseEventDetails(event, getWidget().getElement());
            rpc.edgeClicked(edgeId, details);

            event.stopPropagation();
            event.preventDefault();
        }
    }

    // We must implement getWidget() to cast to correct type
    // (this will automatically create the correct widget type)
    @Override
    public VizComponentWidget getWidget() {
        return (VizComponentWidget) super.getWidget();
    }

    // We must implement getState() to cast to correct type
    @Override
    public VizComponentState getState() {
        return (VizComponentState) super.getState();
    }

    // Whenever the state changes in the server-side, this method is called
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        if (stateChangeEvent.hasPropertyChanged("graph")
                || stateChangeEvent.hasPropertyChanged("graphType")
                || stateChangeEvent.hasPropertyChanged("name")
                || stateChangeEvent.hasPropertyChanged("params")
                || stateChangeEvent.hasPropertyChanged("nodeParams")
                || stateChangeEvent.hasPropertyChanged("edgeParams")) {
            updateGraph();
        }
    }

    private void updateGraph() {
        getWidget().renderGraph(getState());
        getWidget().addNodeClickHandler(new NodeClickHandler());
        getWidget().addEdgeClickHandler(new EdgeClickHandler());
    }
}
