package org.xplorg.tool.telco360.entity;

import java.util.ArrayList;

public class auto_mail_generate_entity {
	
	
	ArrayList<String> kpi_name;
	 String report_type;
	 String report_name;
	 String start_date;
	 String end_date;
	 String start_time;
	 String end_time;
	 String element_type;
	 public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}
	String interval;
		public auto_mail_generate_entity() {
			
		}
	
	public auto_mail_generate_entity(ArrayList<String> kpi_name, String report_type, String report_name,
			String start_date, String end_date, String start_time, String end_time, String element_type,String interval) {

		this.kpi_name = kpi_name;
		this.report_type = report_type;
		this.report_name = report_name;
		this.start_date = start_date;
		this.end_date = end_date;
		this.start_time = start_time;
		this.end_time = end_time;
		this.element_type = element_type;
		this.interval=interval;
	}


	
	
	
	
	
	
	 public ArrayList<String> getKpi_name() {
		return kpi_name;
	}
	public void setKpi_name(ArrayList<String> kpi_name) {
		this.kpi_name = kpi_name;
	}
	public String getReport_type() {
		return report_type;
	}
	public void setReport_type(String report_type) {
		this.report_type = report_type;
	}
	public String getReport_name() {
		return report_name;
	}
	public void setReport_name(String report_name) {
		this.report_name = report_name;
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
	public String getElement_type() {
		return element_type;
	}
	public void setElement_type(String element_type) {
		this.element_type = element_type;
	}

	
}
