package org.xplorg.tool.telco360.tree.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class check_g_s_parent_micro {
	
public String name;
public String type;
public List<gs_child1_micro> children;
	
//public tree_parents_g_s() {}
	
public check_g_s_parent_micro(String name,String type,List<gs_child1_micro> children) {
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
public List<gs_child1_micro> getChildren() {
return children;
}
public void setChildren(List<gs_child1_micro> children) {
this.children = children;
}


}
