
package org.xplorg.tool.telco360.performance.dual_axis;

import java.util.ArrayList;

/**
 * @author Harsh Bali
 * 
 * @apiNote it contains categories & crosshair present under xAxis.
 *
 */
public class dual_axis_3_time_update {
	
	
ArrayList<String>	categories;
boolean crosshair;
String type;
int tickInterval;
date_format_graph labels;
//dateTimeLabelFormats dateTimeLabelFormats;
int tickWidth;
public dual_axis_3_time_update(ArrayList<String> categories, boolean crosshair, String type, int tickInterval,
		date_format_graph labels,
		 int tickWidth) {
	super();
	this.categories = categories;
	this.crosshair = crosshair;
	this.type = type;
	this.tickInterval = tickInterval;
	this.labels = labels;
	//this.dateTimeLabelFormats = dateTimeLabelFormats;
	this.tickWidth = tickWidth;
}
public ArrayList<String> getCategories() {
	return categories;
}
public void setCategories(ArrayList<String> categories) {
	this.categories = categories;
}
public boolean isCrosshair() {
	return crosshair;
}
public void setCrosshair(boolean crosshair) {
	this.crosshair = crosshair;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public int getTickInterval() {
	return tickInterval;
}
public void setTickInterval(int tickInterval) {
	this.tickInterval = tickInterval;
}
public date_format_graph getLabels() {
	return labels;
}
public void setLabels(date_format_graph labels) {
	this.labels = labels;
}

public int getTickWidth() {
	return tickWidth;
}
public void setTickWidth(int tickWidth) {
	this.tickWidth = tickWidth;
}





}
