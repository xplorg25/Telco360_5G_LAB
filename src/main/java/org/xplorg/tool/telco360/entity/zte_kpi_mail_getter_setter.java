package org.xplorg.tool.telco360.entity;

import java.util.ArrayList;

public class zte_kpi_mail_getter_setter {
	ArrayList<String> kpi_name;
	String report_type;
	String start_date;
	String end_date;
	String start_time;
	String end_time;
	String group;
	
	public zte_kpi_mail_getter_setter() {
		
	}
	public zte_kpi_mail_getter_setter(ArrayList<String> kpi_name,String report_type, String start_date, String end_date, String start_time,
			String end_time, String group) {
        this.kpi_name=kpi_name;
		this.report_type = report_type;
		this.start_date = start_date;
		this.end_date = end_date;
		this.start_time = start_time;
		this.end_time = end_time;
		this.group = group;
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
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}

}
