package org.xplorg.tool.telco360.entity;

public class alarm_getr_setr {


String alarm_id;
String alarm_date;
String severity;
String alarm_time;
String alarm_details;
String resolution_time;
String resolution;
String status;
String ticket_no;

public alarm_getr_setr(String ticket_no,String alarm_id, String alarm_date,String alarm_time, String severity, String alarm_details, String resolution_time,String resolution, String status) {
super();
this.alarm_id = alarm_id;
this.alarm_date = alarm_date;
this.severity = severity;
this.alarm_time = alarm_time;
this.resolution_time = resolution_time;
this.resolution = resolution;
this.status = status;
this.ticket_no = ticket_no;
this.alarm_details=alarm_details;
}
public String getAlarm_id() {
	return alarm_id;
}
public void setAlarm_id(String alarm_id) {
	this.alarm_id = alarm_id;
}
public String getSeverity() {
	return severity;
}
public void setSeverity(String severity) {
	this.severity = severity;
}
public String getAlarm_time() {
	return alarm_time;
}
public void setAlarm_time(String alarm_time) {
	this.alarm_time = alarm_time;
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
public String getTicket_no() {
	return ticket_no;
}
public void setTicket_no(String ticket_no) {
	this.ticket_no = ticket_no;
}
public String getAlarm_date() {
	return alarm_date;
}
public void setAlarm_date(String alarm_date) {
	this.alarm_date = alarm_date;
}
public String getAlarm_details() {
	return alarm_details;
}
public void setAlarm_details(String alarm_details) {
	this.alarm_details = alarm_details;
}

	
}
