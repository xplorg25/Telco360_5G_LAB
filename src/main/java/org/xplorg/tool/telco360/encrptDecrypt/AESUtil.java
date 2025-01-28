package org.xplorg.tool.telco360.encrptDecrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {

	 public static String encrypt(String data, String aesKey) throws Exception {
	        SecretKey key = new SecretKeySpec(Base64.getDecoder().decode(aesKey), "AES");
	        Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.ENCRYPT_MODE, key);
	        byte[] encryptedData = cipher.doFinal(data.getBytes());
	        return Base64.getEncoder().encodeToString(encryptedData);
	    }
}
