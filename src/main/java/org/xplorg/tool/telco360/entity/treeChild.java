package org.xplorg.tool.telco360.entity;

public class treeChild {
	
	String name;
	String type="desc";
	String count;
	public treeChild(String name, String count) {
		this.name = name;
		this.count = count;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public String getCount() {
		return count;
	}
	
	

}
