package org.xplorg.tool.telco360.tree.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class gs_child2_micro {
	
public String name;
public String type;
public List<gs_child3a> children;

	
//public tree_parents_g_s() {}
	
public gs_child2_micro(String name,String type,List<gs_child3a> children) {
this.name=name;
this.type=type;
this.children=children;

}
	
	
public List<gs_child3a> getChildren() {
	return children;
}


public void setChildren(List<gs_child3a> children) {
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



}
