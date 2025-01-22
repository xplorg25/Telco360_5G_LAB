package org.xplorg.tool.telco360.entity;

public class resultAlarmSolutionTable {

	private String count;
	private String ticketID;
		private String neName;
		private String alarmName;
	private String sol;
	private String solTime;
	private String rightneName;
	public resultAlarmSolutionTable(String count, String ticketID, String neName, String alarmName, String sol,
			String solTime, String rightneName, String rightAlarmName) {
		super();
		this.count = count;
		this.ticketID = ticketID;
		this.neName = neName;
		this.alarmName = alarmName;
		this.sol = sol;
		this.solTime = solTime;
		this.rightneName = rightneName;
		this.rightAlarmName = rightAlarmName;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getTicketID() {
		return ticketID;
	}
	public void setTicketID(String ticketID) {
		this.ticketID = ticketID;
	}
	public String getNeName() {
		return neName;
	}
	public void setNeName(String neName) {
		this.neName = neName;
	}
	public String getAlarmName() {
		return alarmName;
	}
	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}
	public String getSol() {
		return sol;
	}
	public void setSol(String sol) {
		this.sol = sol;
	}
	public String getSolTime() {
		return solTime;
	}
	public void setSolTime(String solTime) {
		this.solTime = solTime;
	}
	public String getRightneName() {
		return rightneName;
	}
	public void setRightneName(String rightneName) {
		this.rightneName = rightneName;
	}
	public String getRightAlarmName() {
		return rightAlarmName;
	}
	public void setRightAlarmName(String rightAlarmName) {
		this.rightAlarmName = rightAlarmName;
	}
	private String rightAlarmName;



	
	
}
