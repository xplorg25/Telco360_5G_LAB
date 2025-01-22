package org.xplorg.tool.telco360.entity;

public class topology_gis_getter_setter {

String SITEID,OSSCELLIDNAME,VENDOR,REGIONNAME,LATITUDE,LONGITUDE;


public topology_gis_getter_setter(String REGIONNAME,String LATITUDE,String LONGITUDE) {
this.REGIONNAME = REGIONNAME;
this.LATITUDE = LATITUDE;
this.LONGITUDE = LONGITUDE;
}

public topology_gis_getter_setter(String SITEID,String OSSCELLIDNAME,String VENDOR,String REGIONNAME,String LATITUDE,String LONGITUDE) {
this.SITEID = SITEID;
this.OSSCELLIDNAME = OSSCELLIDNAME;
this.VENDOR = VENDOR;
this.REGIONNAME = REGIONNAME;
this.LATITUDE = LATITUDE;
this.LONGITUDE = LONGITUDE;
}

public String getSITEID() {
	return SITEID;
}

public void setSITEID(String sITEID) {
	SITEID = sITEID;
}

public String getOSSCELLIDNAME() {
	return OSSCELLIDNAME;
}

public void setOSSCELLIDNAME(String oSSCELLIDNAME) {
	OSSCELLIDNAME = oSSCELLIDNAME;
}

public String getVENDOR() {
	return VENDOR;
}

public void setVENDOR(String vENDOR) {
	VENDOR = vENDOR;
}

public String getREGIONNAME() {
	return REGIONNAME;
}

public void setREGIONNAME(String rEGIONNAME) {
	REGIONNAME = rEGIONNAME;
}

public String getLATITUDE() {
	return LATITUDE;
}

public void setLATITUDE(String lATITUDE) {
	LATITUDE = lATITUDE;
}

public String getLONGITUDE() {
	return LONGITUDE;
}

public void setLONGITUDE(String lONGITUDE) {
	LONGITUDE = lONGITUDE;
}



}
