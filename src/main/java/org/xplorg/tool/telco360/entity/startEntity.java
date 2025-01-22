package org.xplorg.tool.telco360.entity;

public class startEntity {
	private String alarm ;
	private String[] datesfromA ;
	private int count = 1;
	public startEntity(String alarm, String[] datesfromA) {
		super();
		this.alarm = alarm;
		this.datesfromA = datesfromA;
	}
	public startEntity(String alarm) {
		this.alarm = alarm;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getAlarm() {
		return alarm;
	}
	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}
	public String[] getDatesfromA() {
		return datesfromA;
	}
	public void setDatesfromA(String[] sec1) {
		this.datesfromA = datesfromA;
	}
	@Override
	public String toString() {
		return "startEntity [alarm=" + alarm + ", count=" + count + "]";
	}
	
}
