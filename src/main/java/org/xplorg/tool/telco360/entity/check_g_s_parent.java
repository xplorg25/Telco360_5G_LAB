package org.xplorg.tool.telco360.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class check_g_s_parent {
	
public String name;
public String type;
public List<gs_child1> children;
	
//public tree_parents_g_s() {}
	
public check_g_s_parent(String name,String type,List<gs_child1> children) {
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
public List<gs_child1> getChildren() {
return children;
}
public void setChildren(List<gs_child1> children) {
this.children = children;
}
ArrayList<String> labels;
List<graph_children_getter_setter> datasets;

public check_g_s_parent(ArrayList<String> labels, List<graph_children_getter_setter>datasets) {
this.labels = labels;
this.datasets = datasets;
}

public ArrayList<String> getLabels() {
return labels;
}
public void setLabels(ArrayList<String> labels) {
this.labels = labels;
}
public List<graph_children_getter_setter> getDatasets() {
return datasets;
}
public void setDatasets(List<graph_children_getter_setter> datasets) {
this.datasets = datasets;
}



}
