package org.xplorg.tool.telco360.entity;

public class resultEntityKpi {
	float count;
	String kpi_name;
	String kpi_element_type;
	String domain;
	String element;
	String domain_Alarm;
	String domain_element_name;
	String domain_severity;
	String domain_alarm_desc;
	
	public resultEntityKpi(float count, String kpi_name, String kpi_element_type, String domain, String element,
			String domain_Alarm, String domain_element_name, String domain_severity, String domain_alarm_desc) {
		super();
		this.count = count;
		this.kpi_name = kpi_name;
		this.kpi_element_type = kpi_element_type;
		this.domain = domain;
		this.element = element;
		this.domain_Alarm = domain_Alarm;
		this.domain_element_name = domain_element_name;
		this.domain_severity = domain_severity;
		this.domain_alarm_desc = domain_alarm_desc;
	}

	public void confidenceCount(int countDomain)
	{
		this.count= (this.count/countDomain)*100;
		//count = Count in Relation (X-Y)/ Count in Domain (Y) 
		if(this.count>100) {
			//check if confidence is above 100%
			this.count=100;
		}
	}

	public float getCount() {
		return count;
	}

	public String getKpi_name() {
		return kpi_name;
	}

	public String getKpi_element_type() {
		return kpi_element_type;
	}

	public String getDomain() {
		return domain;
	}

	public String getElement() {
		return element;
	}

	public String getDomain_Alarm() {
		return domain_Alarm;
	}

	public String getDomain_element_name() {
		return domain_element_name;
	}

	public String getDomain_severity() {
		return domain_severity;
	}

	public String getDomain_alarm_desc() {
		return domain_alarm_desc;
	}
}
