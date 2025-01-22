package org.xplorg.tool.telco360.entity;

public class api_getter_setter {
String element_ip;
public api_getter_setter(String element_ip, String element_connected_to, String connected_element_name,
		String element_type) {
	
	this.element_ip = element_ip;
	this.element_connected_to = element_connected_to;
	this.connected_element_name = connected_element_name;
	this.element_type = element_type;
}
String element_connected_to;
String connected_element_name;
String element_type;


public String getElement_ip() {
	return element_ip;
}
public void setElement_ip(String element_ip) {
	this.element_ip = element_ip;
}
public String getElement_connected_to() {
	return element_connected_to;
}
public void setElement_connected_to(String element_connected_to) {
	this.element_connected_to = element_connected_to;
}
public String getConnected_element_name() {
	return connected_element_name;
}
public void setConnected_element_name(String connected_element_name) {
	this.connected_element_name = connected_element_name;
}
public String getElement_type() {
	return element_type;
}
public void setElement_type(String element_type) {
	this.element_type = element_type;
}


	

}
