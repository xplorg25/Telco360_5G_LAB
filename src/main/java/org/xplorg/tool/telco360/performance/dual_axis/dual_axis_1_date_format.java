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





public class dual_axis_1_date_format {


dual_axis_2 chart; //this is for chart (zoom type)
dual_axis_1a title; //for title of graph
dual_axis_1a subtitle; //for title of graph
dual_axis_3_time_update xAxis;// this is for xaxis values
ArrayList<dual_axis_5>	yAxis;//this is for yaxis values
dual_axis_6 tooltip;
ArrayList<dual_axis_7_color>	series;
exporting exporting;





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





public dual_axis_3_time_update getxAxis() {
	return xAxis;
}





public void setxAxis(dual_axis_3_time_update xAxis) {
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





public ArrayList<dual_axis_7_color> getSeries() {
	return series;
}





public void setSeries(ArrayList<dual_axis_7_color> series) {
	this.series = series;
}





public exporting getExporting() {
	return exporting;
}





public void setExporting(exporting exporting) {
	this.exporting = exporting;
}





public dual_axis_1_date_format(dual_axis_2 chart, dual_axis_1a title, dual_axis_1a subtitle,
		dual_axis_3_time_update xAxis, ArrayList<dual_axis_5> yAxis, dual_axis_6 tooltip, ArrayList<dual_axis_7_color> series,
		org.xplorg.tool.telco360.performance.dual_axis.exporting exporting) {
	super();
	this.chart = chart;
	this.title = title;
	this.subtitle = subtitle;
	this.xAxis = xAxis;
	this.yAxis = yAxis;
	this.tooltip = tooltip;
	this.series = series;
	this.exporting = exporting;
}



}
