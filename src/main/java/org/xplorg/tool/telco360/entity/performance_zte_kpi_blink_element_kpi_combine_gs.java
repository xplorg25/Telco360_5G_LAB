package org.xplorg.tool.telco360.entity;

import java.util.ArrayList;

public class performance_zte_kpi_blink_element_kpi_combine_gs {
	
String element_name;
ArrayList<String> kpi_name;



public performance_zte_kpi_blink_element_kpi_combine_gs(String element_name, ArrayList<String> kpi_name) {
	super();
	this.element_name = element_name;
	this.kpi_name = kpi_name;
}
public String getElement_name() {
	return element_name;
}
public void setElement_name(String element_name) {
	this.element_name = element_name;
}
public ArrayList<String> getKpi_name() {
	return kpi_name;
}
public void setKpi_name(ArrayList<String> kpi_name) {
	this.kpi_name = kpi_name;
}


}
