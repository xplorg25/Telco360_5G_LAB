package org.xplorg.tool.telco360.entity;

public class resultEntityTable {
	private String ticketID;
	private String neName;
	private String alarmName;
	private String sol;
	private String solTime;
	public resultEntityTable(String ticketID, String neName, String alarmName, String sol, String solTime) {
		this.ticketID = ticketID;
		this.neName = neName;
		this.alarmName = alarmName;
		this.sol = sol;
		this.solTime = solTime;
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
	@Override
	public String toString() {
		return "resultEntityTable [ticketID=" + ticketID + ", neName=" + neName + ", alarmName=" + alarmName + ", sol="
				+ sol + ", solTime=" + solTime + "]";
	}
	
	
	
	
	
}
