package org.xplorg.tool.telco360.entity;

public class yangDHCP {
	String dlt;
	String mlt;
	String lf;
	yangSUBNET subnet;
	String sharedName ;
	yangSUBNET sharedSubnet;
	public yangDHCP() {
		super();
	}
	public yangDHCP(String dlt, String mlt, String lf, yangSUBNET subnet, String sharedName, yangSUBNET sharedSubnet) {
		super();
		this.dlt = dlt;
		this.mlt = mlt;
		this.lf = lf;
		this.subnet = subnet;
		this.sharedName = sharedName;
		this.sharedSubnet = sharedSubnet;
	}
	public String getDlt() {
		return dlt;
	}
	public void setDlt(String dlt) {
		this.dlt = dlt;
	}
	public String getMlt() {
		return mlt;
	}
	public void setMlt(String mlt) {
		this.mlt = mlt;
	}
	public String getLf() {
		return lf;
	}
	public void setLf(String lf) {
		this.lf = lf;
	}
	public yangSUBNET getSubnet() {
		return subnet;
	}
	public void setSubnet(yangSUBNET subnet) {
		this.subnet = subnet;
	}
	public String getSharedName() {
		return sharedName;
	}
	public void setSharedName(String sharedName) {
		this.sharedName = sharedName;
	}
	public yangSUBNET getSharedSubnet() {
		return sharedSubnet;
	}
	public void setSharedSubnet(yangSUBNET sharedSubnet) {
		this.sharedSubnet = sharedSubnet;
	}
	@Override
	public String toString() {
		return "yangDHCP [dlt=" + dlt + ", mlt=" + mlt + ", lf=" + lf + ", subnet=" + subnet + ", sharedName="
				+ sharedName + ", sharedSubnet=" + sharedSubnet + "]";
	}  
	
}
