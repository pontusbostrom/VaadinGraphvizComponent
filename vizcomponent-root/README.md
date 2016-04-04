#VizComponent Add-on for Vaadin 7

VizComponent is an UI component add-on for Vaadin 7. It used for displaying graphs rendered using a Javascript translation of the Graphviz package, [viz.js](https://github.com/mdaines/viz.js/). On the server side the graphs are represented by the com.vaadin.pontus.vizcomponent.model.Graph.Java class, which provides a hopefully convenient api to modify graphs. The viz.js library is then used in the web-browser to render this graph to an SVG element that is included in the component. The nodes and edges in the SVG are clickable and it is possible to register listeners for clicks server-side. It is also possible to style nodes and edges using CSS in response to clicks. Use of the features of the component is demonstrated in the included demos. The viz.js library is included in this package. The license for viz.js is provided in the viz.js.LICENCE.txt file. For panning an zooming an external library is used https://github.com/ariutta/svg-pan-zoom. It is included in this package. It is released under LGBL.


## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to http://vaadin.com/addon/vizcomponent

## Building and running demo

git clone <url of the VizComponent repository>
cd vizcomponent 
mvn clean install
cd ../vizcomponent-demo
mvn clean vaadin:compile
mvn jetty:run

To see the demo, navigate to http://localhost:8080/

## Development with Eclipse IDE

For further development of this add-on, the following tool-chain is recommended:
- Eclipse IDE
- m2e wtp plug-in (install it from Eclipse Marketplace)
- Vaadin Eclipse plug-in (install it from Eclipse Marketplace)
- JRebel Eclipse plug-in (install it from Eclipse Marketplace)
- Chrome browser

### Importing project

Choose File > Import... > Existing Maven Projects

Note that Eclipse may give "Plugin execution not covered by lifecycle configuration" errors for pom.xml. Use "Permanently mark goal resources in pom.xml as ignored in Eclipse build" quick-fix to mark these errors as permanently ignored in your project. Do not worry, the project still works fine. 

### Debugging server-side

If you have not already compiled the widgetset, do it now by running vaadin:install Maven target for vizcomponent-demo project.

If you have a JRebel license, it makes on the fly code changes faster. Just add JRebel nature to your vizcomponent-demo project by clicking project with right mouse button and choosing JRebel > Add JRebel Nature

To debug project and make code modifications on the fly in the server-side, right-click the vizcomponent-demo project and choose Debug As > Debug on Server. Navigate to http://localhost:8080/vizcomponent-demo/ to see the application.

### Debugging client-side

The most common way of debugging and making changes to the client-side code is dev-mode. To create debug configuration for it, open vizcomponent-demo project properties and click "Create Development Mode Launch" button on the Vaadin tab. Right-click newly added "GWT development mode for vizcomponent-demo.launch" and choose Debug As > Debug Configurations... Open up Classpath tab for the development mode configuration and choose User Entries. Click Advanced... and select Add Folders. Choose Java and Resources under vizcomponent/src/main and click ok. Now you are ready to start debugging the client-side code by clicking debug. Click Launch Default Browser button in the GWT Development Mode in the launched application. Now you can modify and breakpoints to client-side classes and see changes by reloading the web page. 

Another way of debugging client-side is superdev mode. To enable it, uncomment devModeRedirectEnabled line from the end of DemoWidgetSet.gwt.xml located under vizcomponent-demo resources folder and compile the widgetset once by running vaadin:compile Maven target for vizcomponent-demo. Refresh vizcomponent-demo project resources by right clicking the project and choosing Refresh. Click "Create SuperDevMode Launch" button on the Vaadin tab of the vizcomponent-demo project properties panel to create superder mode code server launch configuration and modify the class path as instructed above. After starting the code server by running SuperDevMode launch as Java application, you can navigate to http://localhost:8080/vizcomponent-demo/?superdevmode. Now all code changes you do to your client side will get compiled as soon as you reload the web page. You can also access Java-sources and set breakpoints inside Chrome if you enable source maps from inspector settings. 

 
## Release notes

### Version 0.0.1-SNAPSHOT
- The Graph api on the server side supports a subset of the dot-language. Below is a more detailed list of limitations. 
- Only polygon and ellipse node shapes and arrow head/arrow tails are supported
- Subgraphs are supported to some degree. However, edges to and from subgraph nodes might not give the expected results. It is better to use edges to and from nodes inside the subgraphs instead. 
- Record-based nodes are not supported. However, one can use nodes with HTMl content instead. See the demos. 
- Only the tooltip given by the node id is supported.
- Images in nodes is not supported
- Panning and zooming support is provided via external js-library https://github.com/ariutta/svg-pan-zoom

## Roadmap

This component is developed as a hobby with no public roadmap or any guarantees of upcoming releases. That said, the following features are planned for upcoming releases:


## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:
- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## License & Author

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.txt.

VizComponent is written by Pontus Boström

# Developer Guide

## Getting started

To try out the component, see the demo in the vizcomponent-demo sub-project.

