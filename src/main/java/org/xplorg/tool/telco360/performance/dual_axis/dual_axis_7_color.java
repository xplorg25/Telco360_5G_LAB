/**
 * 
 */
package org.xplorg.tool.telco360.performance.dual_axis;

import java.util.ArrayList;

/**
 * @author Harsh Bali
 * 
 * @apiNote This class contains nme,type,yaxis,data(for lines),tooltip
 * 
 * @apiNote all are under series
 *
 */
public class dual_axis_7_color {
	
String 	name;
String type;
int yAxis;
ArrayList<Double>data;
dual_axis_8 tooltip;
String color;




public dual_axis_7_color(String name, String type, int yAxis, ArrayList<Double> data, dual_axis_8 tooltip,
		String color) {
	super();
	this.name = name;
	this.type = type;
	this.yAxis = yAxis;
	this.data = data;
	this.tooltip = tooltip;
	this.color = color;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public int getyAxis() {
	return yAxis;
}
public void setyAxis(int yAxis) {
	this.yAxis = yAxis;
}
public ArrayList<Double> getData() {
	return data;
}
public void setData(ArrayList<Double> data) {
	this.data = data;
}
public dual_axis_8 getTooltip() {
	return tooltip;
}
public void setTooltip(dual_axis_8 tooltip) {
	this.tooltip = tooltip;
}
public String getColor() {
	return color;
}
public void setColor(String color) {
	this.color = color;
}

}
