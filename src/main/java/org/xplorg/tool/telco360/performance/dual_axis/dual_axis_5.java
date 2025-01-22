/**
 * 
 */
package org.xplorg.tool.telco360.performance.dual_axis;

/**
 * @author Harsh Bali
 * 
 * @apiNote it contains two components . 1st is text which is under title and 2nd is  opposite . Both are under y axis.
 * 
 * @apiNote opposite is to set the axis in parallel or in same direction.
 *
 */
public class dual_axis_5 {

dual_axis_4 title;
boolean opposite;


public dual_axis_5() {}






public dual_axis_5(dual_axis_4 title, boolean opposite) {
	super();
	this.title = title;
	this.opposite = opposite;
}






public dual_axis_4 getTitle() {
	return title;
}


public void setTitle(dual_axis_4 title) {
	this.title = title;
}


public boolean isOpposite() {
	return opposite;
}


public void setOpposite(boolean opposite) {
	this.opposite = opposite;
}



	
	
}



