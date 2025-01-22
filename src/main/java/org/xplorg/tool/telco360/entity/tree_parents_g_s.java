package org.xplorg.tool.telco360.entity;

import java.util.ArrayList;
import java.util.List;

public class tree_parents_g_s {
	
public String name;
public String type;
public List<tree_children_g_s> children;
	
public tree_parents_g_s() {
	
}
	
public tree_parents_g_s(String name,String type,List<tree_children_g_s> children) {
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
ArrayList<String> labels;
List<graph_children_getter_setter> datasets;

public tree_parents_g_s(ArrayList<String> labels, List<graph_children_getter_setter>datasets) {
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
