package org.xplorg.tool.telco360.entity;

import java.util.ArrayList;

public class graph_parent_getter_setter {

	ArrayList<String> labels;
	ArrayList<graph_children_getter_setter> datasets;

	public graph_parent_getter_setter(ArrayList<String> labels, ArrayList<graph_children_getter_setter> datasets) {
	this.labels = labels;
	this.datasets = datasets;
	}

	public ArrayList<String> getLabels() {
		return labels;
	}

	public void setLabels(ArrayList<String> labels) {
		this.labels = labels;
	}

	public ArrayList<graph_children_getter_setter> getDatasets() {
		return datasets;
	}

	public void setDatasets(ArrayList<graph_children_getter_setter> datasets) {
		this.datasets = datasets;
	}

	
}
