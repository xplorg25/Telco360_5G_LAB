/**
 * This 
 */
package org.xplorg.tool.telco360.performance.dual_axis;

import java.util.ArrayList;

/**
 * @author Harsh Bali
 * 
 * @apiNote it contains categories & crosshair present under xAxis.
 *
 */
public class dual_axis_3 {
	
	
ArrayList<String>	categories;
boolean crosshair;

public dual_axis_3() {
	
}

public dual_axis_3(ArrayList<String> categories, boolean crosshair) {
	super();
	this.categories = categories;
	this.crosshair = crosshair;
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




}
