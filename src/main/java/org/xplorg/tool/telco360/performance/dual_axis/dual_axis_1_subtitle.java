/**
 * 
 * 
 */
package org.xplorg.tool.telco360.performance.dual_axis;

import java.util.ArrayList;

/**
 * @author Harsh Bali
 *
 *@apiNote This is the main class where all the classes are join together to create a single json.
 */





public class dual_axis_1_subtitle {


dual_axis_2 chart; //this is for chart (zoom type)
dual_axis_1a title; //for title of graph
dual_axis_1a subtitle; //for title of graph
dual_axis_3 xAxis;// this is for xaxis values
ArrayList<dual_axis_5>	yAxis;//this is for yaxis values
dual_axis_6 tooltip;
ArrayList<dual_axis_7>	series;

public dual_axis_2 getChart() {
	return chart;
}

public void setChart(dual_axis_2 chart) {
	this.chart = chart;
}

public dual_axis_1a getTitle() {
	return title;
}

public void setTitle(dual_axis_1a title) {
	this.title = title;
}

public dual_axis_1a getSubtitle() {
	return subtitle;
}

public void setSubtitle(dual_axis_1a subtitle) {
	this.subtitle = subtitle;
}

public dual_axis_3 getxAxis() {
	return xAxis;
}

public void setxAxis(dual_axis_3 xAxis) {
	this.xAxis = xAxis;
}

public ArrayList<dual_axis_5> getyAxis() {
	return yAxis;
}

public void setyAxis(ArrayList<dual_axis_5> yAxis) {
	this.yAxis = yAxis;
}

public dual_axis_6 getTooltip() {
	return tooltip;
}

public void setTooltip(dual_axis_6 tooltip) {
	this.tooltip = tooltip;
}

public ArrayList<dual_axis_7> getSeries() {
	return series;
}

public void setSeries(ArrayList<dual_axis_7> series) {
	this.series = series;
}

public dual_axis_1_subtitle(dual_axis_2 chart, dual_axis_1a title, dual_axis_1a subtitle, dual_axis_3 xAxis,
		ArrayList<dual_axis_5> yAxis, dual_axis_6 tooltip, ArrayList<dual_axis_7> series) {
	super();
	this.chart = chart;
	this.title = title;
	this.subtitle = subtitle;
	this.xAxis = xAxis;
	this.yAxis = yAxis;
	this.tooltip = tooltip;
	this.series = series;
}






}
