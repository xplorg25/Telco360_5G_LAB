package org.xplorg.tool.telco360.entity;

public class resultEntity {
	
	private String leftError;
	private String rightError;
	private float count;
	
	public resultEntity(String leftError, String rightError, float count) {
		this.leftError = leftError;
		this.rightError = rightError;
		this.count = count;
	}

	public String getLeftError() {
		return leftError;
	}

	public void setLeftError(String leftError) {
		this.leftError = leftError;
	}

	public String getRightError() {
		return rightError;
	}

	public void setRightError(String rightError) {
		this.rightError = rightError;
	}

	public float getCount() {
		return count;
	}

	public void setCount(float count) {
		this.count = count;
	}
	
	public void confidenceCount(int countDomain)
	{

	//System.out.println(this.count+">>>"+countDomain);
		this.count= (this.count/countDomain)*100;
		//System.out.println(this.count+">>>"+countDomain);
		//count = Count in Relation (X-Y)/ Count in Domain (Y) 
		if(this.count>100) {
			//check if confidence is above 100%
			this.count=100;
		}
	}

	@Override
	public String toString() {
		return "resultEntity [leftError=" + leftError + ", rightError=" + rightError + ", count=" + count + "]";
	}




}
