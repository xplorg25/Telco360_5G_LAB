package org.xplorg.tool.telco360.entity;

public class analysisLogicEntity {
String elementX;
String alarmX;
String elementY;
String alarmY;
String confidence;
public analysisLogicEntity(String elementX, String alarmX, String elementY, String alarmY, String confidence) {
	super();
	this.elementX = elementX;
	this.alarmX = alarmX;
	this.elementY = elementY;
	this.alarmY = alarmY;
	this.confidence = confidence;
}
public String getElementX() {
	return elementX;
}
public void setElementX(String elementX) {
	this.elementX = elementX;
}
public String getAlarmX() {
	return alarmX;
}
public void setAlarmX(String alarmX) {
	this.alarmX = alarmX;
}
public String getElementY() {
	return elementY;
}
public void setElementY(String elementY) {
	this.elementY = elementY;
}
public String getAlarmY() {
	return alarmY;
}
public void setAlarmY(String alarmY) {
	this.alarmY = alarmY;
}
public String getConfidence() {
	return confidence;
}
public void setConfidence(String confidence) {
	this.confidence = confidence;
}



}
