package org.xplorg.tool.telco360.entity;

public class solutionManuplation {
		@Override
	public String toString() {
		return "solutionManuplation [alarm1=" + alarm1 + ", desc1=" + desc1 + ", alarm2=" + alarm2 + ", desc2=" + desc2
				+ "]";
	}


		private String alarm1;
		private String desc1;
		private String alarm2;
		private String desc2;
		
		public solutionManuplation() {
		}
		public solutionManuplation(String alarm1, String desc1, String alarm2, String desc2) {
			this.alarm1 = alarm1;
			this.desc1 = desc1;
			this.alarm2 = alarm2;
			this.desc2 = desc2;
		}
		public String getAlarm1() {
			return alarm1;
		}


		public void setAlarm1(String alarm1) {
			this.alarm1 = alarm1;
		}


		public String getDesc1() {
			return desc1;
		}


		public void setDesc1(String desc1) {
			this.desc1 = desc1;
		}


		public String getAlarm2() {
			return alarm2;
		}


		public void setAlarm2(String alarm2) {
			this.alarm2 = alarm2;
		}


		public String getDesc2() {
			return desc2;
		}


		public void setDesc2(String desc2) {
			this.desc2 = desc2;
		}



}
