package org.xplorg.tool.telco360.entity;

public class resultKpiLive {
	
	private String date;
	private String time;
	private String alarm;
	private String element;
	private String severity;
	private String desc;
	public resultKpiLive(String date, String time, String alarm, String element, String severity, String desc) {
		super();
		this.date = date;
		this.time = time;
		this.alarm = alarm;
		this.element = element;
		this.severity = severity;
		this.desc = desc;
	}
	public String getDate() {
		return date;
	}
	public String getTime() {
		return time;
	}
	public String getAlarm() {
		return alarm;
	}
	public String getElement() {
		return element;
	}
	public String getSeverity() {
		return severity;
	}
	public String getDesc() {
		return desc;
	}
	

}
