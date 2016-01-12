package com.vaadin.pontus.vizcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@JavaScript("viz.js")
public class JSLoader extends AbstractJavaScriptExtension {

    public JSLoader(UI ui) {
        extend(ui);
    }
}
