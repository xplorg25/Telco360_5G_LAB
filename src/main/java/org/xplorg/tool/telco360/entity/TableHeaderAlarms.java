package org.xplorg.tool.telco360.entity;

public class TableHeaderAlarms {
	String field;
	String header;
	boolean visible;


	public TableHeaderAlarms(String header, String field, boolean visible) {
		this.field = field;
		this.header = header;
		this.visible = visible;
	}
	
	
	public String getField() {
		return field;
	}


	public void setField(String field) {
		this.field = field;
	}


	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}


	public boolean getVisible() {
		return visible;
	}


	public void setVisible(boolean visible) {
		this.visible = visible;
	}


	@Override
	public String toString() {
		return "tableHeaderAlarms [field=" + field + ", header=" + header + ", visible=" + visible + "]";
	}
	
}
