/**
 * 
 * 
 */
package org.xplorg.tool.telco360.performance.dual_axis;

/**
 * @author Harsh Bali
 * 
 * @apiNote  This is for zoomType under chart.
 *
 */


public class dual_axis_2 {
String zoomType;
int width;
int height;

public dual_axis_2(String zoomType, int width, int height) {
	super();
	this.zoomType = zoomType;
	this.width = width;
	this.height = height;
}

public int getWidth() {
	return width;
}

public void setWidth(int width) {
	this.width = width;
}

public int getHeight() {
	return height;
}

public void setHeight(int height) {
	this.height = height;
}

public dual_axis_2() {
	
}


public String getZoomType() {
	return zoomType;
}

public void setZoomType(String zoomType) {
	this.zoomType = zoomType;
}

}
