package org.xplorg.tool.telco360.entity;

import java.util.List;

public class tree_parents_t_d_final {
	
public String name;
public String type;
public List<tree_parents_t_d> children;
	
//public tree_parents_g_s() {}
	
public tree_parents_t_d_final(String name,String type,List<tree_parents_t_d> children) {
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
public List<tree_parents_t_d> getChildren() {
return children;
}
public void setChildren(List<tree_parents_t_d> children) {
this.children = children;
}
}
