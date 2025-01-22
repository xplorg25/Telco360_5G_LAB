package org.xplorg.tool.telco360.entity;

import java.util.ArrayList;

public class treeParent {
	
	String name;
	String type="head";
	ArrayList<treeMid> children;
	public treeParent(String name, ArrayList<treeMid> children) {
		this.name = name;
		this.children = children;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public ArrayList<treeMid> getChildren() {
		return children;
	}
	
	

}
