package org.xplorg.tool.telco360.entity;

import java.util.List;

public class tree_parents_t_d {

public String name;
public String type;
public List<tree_children_g_s> children;


public tree_parents_t_d(String name,String type,List<tree_children_g_s> children) {
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
public List<tree_children_g_s> getChildren() {
return children;
}
public void setChildren(List<tree_children_g_s> children) {
this.children = children;
}
	
	
}
