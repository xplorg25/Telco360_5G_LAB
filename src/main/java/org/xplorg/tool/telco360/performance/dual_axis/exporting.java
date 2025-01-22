package org.xplorg.tool.telco360.performance.dual_axis;

public class exporting {
	
boolean enabled;
boolean showTable;
String fileName;





public exporting(boolean enabled, boolean showTable, String fileName) {
	super();
	this.enabled = enabled;
	this.showTable = showTable;
	this.fileName = fileName;
}
public boolean isEnabled() {
	return enabled;
}
public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}
public boolean isShowTable() {
	return showTable;
}
public void setShowTable(boolean showTable) {
	this.showTable = showTable;
}
public String getFileName() {
	return fileName;
}
public void setFileName(String fileName) {
	this.fileName = fileName;
}




}
