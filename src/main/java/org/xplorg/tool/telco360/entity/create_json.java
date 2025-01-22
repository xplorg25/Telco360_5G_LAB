package org.xplorg.tool.telco360.entity;

import java.util.ArrayList;

public class create_json {
String admin_id;	
String graph_name;
String graph_count;
String domain;
String vendor;
String elementname;
String ip;
String interfacee;
ArrayList<String>kpi_list;
String graph_type;
String sla_threshold;


public create_json() {
	
}


public create_json(String admin_id, String graph_name, String graph_count, String domain, String vendor,
		String elementname, String ip, String interfacee, ArrayList<String> kpi_list, String graph_type,
		String sla_threshold) {
	super();
	this.admin_id = admin_id;
	this.graph_name = graph_name;
	this.graph_count = graph_count;
	this.domain = domain;
	this.vendor = vendor;
	this.elementname = elementname;
	this.ip = ip;
	this.interfacee = interfacee;
	this.kpi_list = kpi_list;
	this.graph_type = graph_type;
	this.sla_threshold = sla_threshold;
}


public String getAdmin_id() {
	return admin_id;
}


public void setAdmin_id(String admin_id) {
	this.admin_id = admin_id;
}


public String getGraph_name() {
	return graph_name;
}


public void setGraph_name(String graph_name) {
	this.graph_name = graph_name;
}


public String getGraph_count() {
	return graph_count;
}


public void setGraph_count(String graph_count) {
	this.graph_count = graph_count;
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


public String getElementname() {
	return elementname;
}


public void setElementname(String elementname) {
	this.elementname = elementname;
}


public String getIp() {
	return ip;
}


public void setIp(String ip) {
	this.ip = ip;
}


public String getInterfacee() {
	return interfacee;
}


public void setInterfacee(String interfacee) {
	this.interfacee = interfacee;
}


public ArrayList<String> getKpi_list() {
	return kpi_list;
}


public void setKpi_list(ArrayList<String> kpi_list) {
	this.kpi_list = kpi_list;
}


public String getGraph_type() {
	return graph_type;
}


public void setGraph_type(String graph_type) {
	this.graph_type = graph_type;
}


public String getSla_threshold() {
	return sla_threshold;
}


public void setSla_threshold(String sla_threshold) {
	this.sla_threshold = sla_threshold;
}



}