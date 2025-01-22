package org.xplorg.tool.telco360.entity;

public class kpi_insert_columns {
String opco;
String admin_id;

String domain;
String vendor;
String kpi_name;
String group;
String actual_formula;
String formula;
String threshold;
//unit;
String topology;
String rate;
String element;
String calculation;
String trouble_ticket;
String severity;

public kpi_insert_columns() {
	
}




public kpi_insert_columns(String opco, String admin_id, String domain, String vendor, String kpi_name, String group,
		String actual_formula, String formula, String threshold, String topology, String rate, String element,
		String calculation, String trouble_ticket, String severity) {
	super();
	this.opco = opco;
	this.admin_id = admin_id;
	this.domain = domain;
	this.vendor = vendor;
	this.kpi_name = kpi_name;
	this.group = group;
	this.actual_formula = actual_formula;
	this.formula = formula;
	this.threshold = threshold;
	this.topology = topology;
	this.rate = rate;
	this.element = element;
	this.calculation = calculation;
	this.trouble_ticket = trouble_ticket;
	this.severity = severity;
}




public String getOpco() {
	return opco;
}

public void setOpco(String opco) {
	this.opco = opco;
}

public String getAdmin_id() {
	return admin_id;
}

public void setAdmin_id(String admin_id) {
	this.admin_id = admin_id;
}

public String getDomain() {
	return domain;
}

public void setDomain(String domain) {
	this.domain = domain;
}

public String getVendor() {
	return vendor;
}

public void setVendor(String vendor) {
	this.vendor = vendor;
}

public String getKpi_name() {
	return kpi_name;
}

public void setKpi_name(String kpi_name) {
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

public String getTrouble_ticket() {
	return trouble_ticket;
}

public void setTrouble_ticket(String trouble_ticket) {
	this.trouble_ticket = trouble_ticket;
}

public String getSeverity() {
	return severity;
}

public void setSeverity(String severity) {
	this.severity = severity;
}









	
}
