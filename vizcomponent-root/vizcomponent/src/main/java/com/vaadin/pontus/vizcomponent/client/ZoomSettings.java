package com.vaadin.pontus.vizcomponent.client;

public class ZoomSettings {
	
	boolean panEnabled =  true;
	boolean controlIconsEnabled  = false;
	boolean zoomEnabled = true;
	boolean dblClickZoomEnabled = true;
	boolean mouseWheelZoomEnabled = true;
	boolean preventMouseEventsDefault = false;
	float zoomScaleSensitivity = 0.2f;
	float minZoom = 0.1f;
	float maxZoom = 10;
	boolean fit = true;
	boolean contain = false;
	boolean center = true;
	//String refreshRate = AUTO; // TODO: according to the docu this  ist 'auto' or a number
	
	public boolean isPanEnabled() {
		return panEnabled;
	}
	public void setPanEnabled(boolean panEnabled) {
		this.panEnabled = panEnabled;
	}
	public boolean isControlIconsEnabled() {
		return controlIconsEnabled;
	}
	public void setControlIconsEnabled(boolean controlIconsEnabled) {
		this.controlIconsEnabled = controlIconsEnabled;
	}
	public boolean isZoomEnabled() {
		return zoomEnabled;
	}
	public void setZoomEnabled(boolean zoomEnabled) {
		this.zoomEnabled = zoomEnabled;
	}
	public boolean isDblClickZoomEnabled() {
		return dblClickZoomEnabled;
	}
	public void setDblClickZoomEnabled(boolean dblClickZoomEnabled) {
		this.dblClickZoomEnabled = dblClickZoomEnabled;
	}
	public boolean isMouseWheelZoomEnabled() {
		return mouseWheelZoomEnabled;
	}
	public void setMouseWheelZoomEnabled(boolean mouseWheelZoomEnabled) {
		this.mouseWheelZoomEnabled = mouseWheelZoomEnabled;
	}
	public boolean isPreventMouseEventsDefault() {
		return preventMouseEventsDefault;
	}
	public void setPreventMouseEventsDefault(boolean preventMouseEventsDefault) {
		this.preventMouseEventsDefault = preventMouseEventsDefault;
	}
	public float getZoomScaleSensitivity() {
		return zoomScaleSensitivity;
	}
	public void setZoomScaleSensitivity(float zoomScaleSensitivity) {
		this.zoomScaleSensitivity = zoomScaleSensitivity;
	}
	public float getMinZoom() {
		return minZoom;
	}
	public void setMinZoom(float minZoom) {
		this.minZoom = minZoom;
	}
	public float getMaxZoom() {
		return maxZoom;
	}
	public void setMaxZoom(float maxZoom) {
		this.maxZoom = maxZoom;
	}
	public boolean isFit() {
		return fit;
	}
	public void setFit(boolean fit) {
		this.fit = fit;
	}
	public boolean isContain() {
		return contain;
	}
	public void setContain(boolean contain) {
		this.contain = contain;
	}
	public boolean isCenter() {
		return center;
	}
	public void setCenter(boolean center) {
		this.center = center;
	}
	
}
