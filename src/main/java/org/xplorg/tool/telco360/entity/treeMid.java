package org.xplorg.tool.telco360.entity;

import java.util.ArrayList;

public class treeMid {
	String name;
	String type="head";
	ArrayList<treeChild> children;
	public treeMid(String name, ArrayList<treeChild> children) {
		this.name = name;
		this.children = children;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public ArrayList<treeChild> getChildren() {
		return children;
	}
}
