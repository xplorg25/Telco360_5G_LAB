package org.xplorg.tool.telco360.entity;

public class yangSUBNET {
	String net;
	String mask;
	String dbp;
	String rangeLow;
	String rangeHigh;
	String router;
	String mlt;
	
	public yangSUBNET() {
		super();
	}
	public yangSUBNET(String net, String mask, String dbp, String rangeLow, String rangeHigh, String router,
			String mlt) {
		super();
		this.net = net;
		this.mask = mask;
		this.dbp = dbp;
		this.rangeLow = rangeLow;
		this.rangeHigh = rangeHigh;
		this.router = router;
		this.mlt = mlt;
	}
	public String getNet() {
		return net;
	}
	public void setNet(String net) {
		this.net = net;
	}
	public String getMask() {
		return mask;
	}
	public void setMask(String mask) {
		this.mask = mask;
	}
	public String getDbp() {
		return dbp;
	}
	public void setDbp(String dbp) {
		this.dbp = dbp;
	}
	public String getRangeLow() {
		return rangeLow;
	}
	public void setRangeLow(String rangeLow) {
		this.rangeLow = rangeLow;
	}
	public String getRangeHigh() {
		return rangeHigh;
	}
	public void setRangeHigh(String rangeHigh) {
		this.rangeHigh = rangeHigh;
	}
	public String getRouter() {
		return router;
	}
	public void setRouter(String router) {
		this.router = router;
	}
	public String getMlt() {
		return mlt;
	}
	public void setMlt(String mlt) {
		this.mlt = mlt;
	}
	@Override
	public String toString() {
		return "yangSUBNET [net=" + net + ", mask=" + mask + ", dbp=" + dbp + ", rangeLow=" + rangeLow + ", rangeHigh="
				+ rangeHigh + ", router=" + router + ", mlt=" + mlt + "]";
	}
	
}
