package org.xplorg.tool.telco360.entity;

public class update_resolution_getr_setr {

String resolution;

String resolution_time;

String alarm_id;

String status;

public update_resolution_getr_setr(String resolution, String resolution_time, String alarm_id, String status) {
super();
this.resolution = resolution;
this.resolution_time = resolution_time;
this.alarm_id = alarm_id;
this.status = status;
}

public String getAlarm_id() {
	return alarm_id;
}

public void setAlarm_id(String alarm_id) {
	this.alarm_id = alarm_id;
}


public String getResolution_time() {
	return resolution_time;
}

public void setResolution_time(String resolution_time) {
	this.resolution_time = resolution_time;
}

public String getResolution() {
	return resolution;
}

public void setResolution(String resolution) {
	this.resolution = resolution;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}


public update_resolution_getr_setr() {
	
}
	
}
