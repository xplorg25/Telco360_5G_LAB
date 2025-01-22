package org.xplorg.tool.telco360.entity;

public class kpi_details_getter_setter {
	
	
	public kpi_details_getter_setter(String formula, String group, String threshold, String element_name) {
		
		this.formula = formula;
		this.group = group;
		this.threshold = threshold;
		this.element_name = element_name;
	}
	String formula;
	String group;
	String threshold;
	String element_name;
	
	
	
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getThreshold() {
		return threshold;
	}
	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}
	public String getElement_name() {
		return element_name;
	}
	public void setElement_name(String element_name) {
		this.element_name = element_name;
	}
	
	
	

}
