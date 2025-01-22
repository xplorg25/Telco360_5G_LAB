package org.xplorg.tool.telco360.restController;

import java.util.Base64;

public class test {

	public static void main(String[] args) {
	
		 String originalString = "HarshBali";
	        String base64String = base64Encode(originalString);
	        System.out.println("Encoded String: " + base64String);
	}
    public static String base64Encode(String originalString) {
    	byte[] encodedBytes = Base64.getEncoder().encode(originalString.getBytes());
        return new String(encodedBytes);
    }
}
