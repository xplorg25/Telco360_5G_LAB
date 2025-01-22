package org.xplorg.tool.telco360.tree.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.xplorg.tool.telco360.entity.gs_child3;
@XmlRootElement
public class gs_child7_micro {
	
public String name;
public String type;
public List<gs_child3> children;




public gs_child7_micro(String name, String type, List<gs_child3> children) {
	super();
	this.name = name;
	this.type = type;
	this.children = children;
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
public List<gs_child3> getChildren() {
	return children;
}
public void setChildren(List<gs_child3> children) {
	this.children = children;
}

	

}