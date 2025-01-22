package org.xplorg.tool.telco360.entity;

public class TableHeader {
	String field;
	String header;


	public TableHeader(String header, String field) {
		this.field = field;
		this.header = header;
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


	@Override
	public String toString() {
		return "tableHeader [field=" + field + ", header=" + header + "]";
	}
	
}
