package com.vaadin.pontus.vizcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.UI;

/**
 * Class to load the Javascript library viz.js. This is done by the Javascript
 * annotation and the constructor.
 *
 * @author Pontus Boström
 *
 */
@SuppressWarnings("serial")
@JavaScript("viz.js")
class JSLoader extends AbstractJavaScriptExtension {

    public JSLoader(UI ui) {
        extend(ui);
    }
}
