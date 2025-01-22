package org.xplorg.tool.telco360.tree.entity;

import java.util.List;

public class gs_child1_micro {
	public String name;
	public String type;
	
	public List<gs_child2_micro> children;

	public gs_child1_micro(String name,String type,List<gs_child2_micro> children) {
		this.name=name;
		this.type=type;
		this.children=children;
		
	}

public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public List<gs_child2_micro> getChildren() {
		return children;
	}

	public void setChildren(List<gs_child2_micro> children) {
		this.children = children;
	}





}
