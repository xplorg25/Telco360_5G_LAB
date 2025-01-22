package org.xplorg.tool.telco360.entity;

public class auto_report_mechanism {
	
String 	domain;
String 	vendor;
String 	element;
String 	report_name;
String 	report_type;
String 	report_duration;
String 	start_time;
String 	end_time;
String 	start_date;
String 	end_date;
String 	reporting_time;
String 	reporting_date;
String 	reporting;

public auto_report_mechanism() {
	
}

public auto_report_mechanism(String domain, String vendor, String element, String report_name, String report_type,
		String report_duration, String start_time, String end_time, String start_date, String end_date,
		String reporting_time, String reporting_date, String reporting) {

	this.domain = domain;
	this.vendor = vendor;
	this.element = element;
	this.report_name = report_name;
	this.report_type = report_type;
	this.report_duration = report_duration;
	this.start_time = start_time;
	this.end_time = end_time;
	this.start_date = start_date;
	this.end_date = end_date;
	this.reporting_time = reporting_time;
	this.reporting_date = reporting_date;
	this.reporting = reporting;
}

	


public String getDomain() {
	return domain;
}
public void setDomain(String domain) {
	this.domain = domain;
}
public String getVendor() {
	return vendor;
}
public void setVendor(String vendor) {
	this.vendor = vendor;
}
public String getElement() {
	return element;
}
public void setElement(String element) {
	this.element = element;
}
public String getReport_name() {
	return report_name;
}
public void setReport_name(String report_name) {
	this.report_name = report_name;
}
public String getReport_type() {
	return report_type;
}
public void setReport_type(String report_type) {
	this.report_type = report_type;
}
public String getReport_duration() {
	return report_duration;
}
public void setReport_duration(String report_duration) {
	this.report_duration = report_duration;
}
public String getStart_time() {
	return start_time;
}
public void setStart_time(String start_time) {
	this.start_time = start_time;
}
public String getEnd_time() {
	return end_time;
}
public void setEnd_time(String end_time) {
	this.end_time = end_time;
}
public String getStart_date() {
	return start_date;
}
public void setStart_date(String start_date) {
	this.start_date = start_date;
}
public String getEnd_date() {
	return end_date;
}
public void setEnd_date(String end_date) {
	this.end_date = end_date;
}
public String getReporting_time() {
	return reporting_time;
}
public void setReporting_time(String reporting_time) {
	this.reporting_time = reporting_time;
}
public String getReporting_date() {
	return reporting_date;
}
public void setReporting_date(String reporting_date) {
	this.reporting_date = reporting_date;
}
public String getReporting() {
	return reporting;
}
public void setReporting(String reporting) {
	this.reporting = reporting;
}

	

}
