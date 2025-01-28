package org.xplorg.tool.telco360.encrptDecrypt;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Base64;
import java.security.PrivateKey;
import java.security.KeyFactory;


import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtil {
	// Generate RSA Key Pair (Private and Public Keys)
    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048, new SecureRandom()); // 2048-bit RSA key pair
        return keyGen.generateKeyPair();
    }

    // Extract the Public and Private Keys
    public static String getPublicKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    public static String getPrivateKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }
    
 // Decrypt the AES key using the RSA private key
    public static String decryptAESKey(String encryptedAESKey, String privateKey) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(privateKey);  // Decode the private key
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedAESKey));
        return new String(decryptedBytes);  // Decrypted AES key
    }
}
