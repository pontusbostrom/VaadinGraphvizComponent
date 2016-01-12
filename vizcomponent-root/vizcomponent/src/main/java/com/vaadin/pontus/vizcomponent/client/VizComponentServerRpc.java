package com.vaadin.pontus.vizcomponent.client;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

// ServerRpc is used to pass events from client to server
public interface VizComponentServerRpc extends ServerRpc {

    public void nodeClicked(String nodeId, MouseEventDetails mouseDetails);

    public void edgeClicked(String edgeId, MouseEventDetails mouseDetails);

}
