package org.xplorg.tool.telco360.entity;

import java.util.ArrayList;

public class user_specific_kpi_excel {
String opco;
String user_id;
String event_name;
ArrayList<String> kpi_name;
String group;
String actual_formula;
String formula;
String threshold;
String topology;
String rate;
String calculation;
String domain;
String vendor;

String element;
ArrayList<String> element_list;
String interface_select;




public user_specific_kpi_excel() {
	
}

public user_specific_kpi_excel(String opco,String user_id, String event_name, ArrayList<String> kpi_name, String group,
		String actual_formula, String formula, String threshold, String topology, String rate, String domain,
		String element, String calculation,String vendor,ArrayList<String> element_list,String interface_select) {
	super();
	this.opco=opco;
	this.user_id = user_id;
	this.event_name = event_name;
	this.kpi_name = kpi_name;
	this.group = group;
	this.actual_formula = actual_formula;
	this.formula = formula;
	this.threshold = threshold;
	this.topology = topology;
	this.rate = rate;
    this.calculation = calculation;
	this.domain = domain;
	this.vendor=vendor;
	this.element = element;
	this.element_list=element_list;
	this.interface_select=interface_select;
	
	
	
}


public String getInterface_select() {
	return interface_select;
}

public void setInterface_select(String interface_select) {
	this.interface_select = interface_select;
}

public ArrayList<String> getElement_list() {
	return element_list;
}

public void setElement_list(ArrayList<String> element_list) {
	this.element_list = element_list;
}

public String getVendor() {
	return vendor;
}

public void setVendor(String vendor) {
	this.vendor = vendor;
}


public String getUser_id() {
	return user_id;
}
public void setUser_id(String user_id) {
	this.user_id = user_id;
}
public String getEvent_name() {
	return event_name;
}
public void setEvent_name(String event_name) {
	this.event_name = event_name;
}
public ArrayList<String> getKpi_name() {
	return kpi_name;
}
public void setKpi_name(ArrayList<String> kpi_name) {
	this.kpi_name = kpi_name;
}
public String getGroup() {
	return group;
}
public void setGroup(String group) {
	this.group = group;
}
public String getActual_formula() {
	return actual_formula;
}
public void setActual_formula(String actual_formula) {
	this.actual_formula = actual_formula;
}
public String getFormula() {
	return formula;
}
public void setFormula(String formula) {
	this.formula = formula;
}
public String getThreshold() {
	return threshold;
}
public void setThreshold(String threshold) {
	this.threshold = threshold;
}
public String getTopology() {
	return topology;
}
public void setTopology(String topology) {
	this.topology = topology;
}
public String getRate() {
	return rate;
}
public void setRate(String rate) {
	this.rate = rate;
}
public String getDomain() {
	return domain;
}
public void setDomain(String domain) {
	this.domain = domain;
}
public String getElement() {
	return element;
}
public void setElement(String element) {
	this.element = element;
}
public String getCalculation() {
	return calculation;
}
public void setCalculation(String calculation) {
	this.calculation = calculation;
}


public String getOpco() {
	return opco;
}

public void setOpco(String opco) {
	this.opco = opco;
}
		

}
