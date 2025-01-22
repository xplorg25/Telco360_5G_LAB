package org.xplorg.tool.telco360.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class gs_child2 {
	
public String name;
public String type;
public List<gs_child3> children;

	
//public tree_parents_g_s() {}
	
public gs_child2(String name,String type,List<gs_child3> children) {
this.name=name;
this.type=type;
this.children=children;

}
	
	
public List<gs_child3> getChildren() {
	return children;
}


public void setChildren(List<gs_child3> children) {
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

ArrayList<String> labels;
List<graph_children_getter_setter> datasets;

public gs_child2(ArrayList<String> labels, List<graph_children_getter_setter>datasets) {
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
