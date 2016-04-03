package com.vaadin.pontus.vizcomponent.demo;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("VizComponent Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "com.vaadin.pontus.vizcomponent.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {

    }

    @Override
    protected void init(VaadinRequest request) {

        Label label = new Label("<h1>Demo of the Graphviz component</h1>",
                ContentMode.HTML);
        label.setHeightUndefined();

        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        TabSheet tabs = new TabSheet();
        tabs.setSizeFull();

        layout.addComponent(label);
        layout.addComponent(tabs);
        layout.setMargin(true);
        layout.setExpandRatio(tabs, 1);
        setContent(layout);

        tabs.addTab(new SimpleDemoView(), "Simple demo");
        tabs.addTab(new MoreComplexDemoView(), "More complex demo");
        tabs.addTab(new UMLDemoView(), "An UML demo");
        tabs.addTab(new InteractiveDemoView(), "Interactive demo");
        tabs.addTab(new SubgraphDemoView(), "Subgraph demo");

    }

}
