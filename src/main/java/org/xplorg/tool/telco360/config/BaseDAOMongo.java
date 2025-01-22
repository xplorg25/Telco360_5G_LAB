package org.xplorg.tool.telco360.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.MongoClient;

public class BaseDAOMongo {

Logger log = LogManager.getLogger(BaseDAOMongo.class.getName());	

public static ArrayList<String>userIds=new ArrayList<String>();
public static ArrayList<String>sessionIds=new ArrayList<String>();
public static ArrayList<String>userSubscription=new ArrayList<String>();
public static ArrayList<String>userSshSubscription=new ArrayList<String>();

public MongoClient getConnection()
{
if(log.isDebugEnabled()) {
log.debug("*****************************************  checked into getConnection of BaseDAOMongo  ************************************************");	
}		
try{
Properties config=getProperties();
MongoClient mongo = new MongoClient(config.getProperty("database.mongodb.ipaddress"),Integer.parseInt(config.getProperty("database.mongodb.port")));
//MongoDatabase database = mongo.getDatabase(dbname);	
return mongo;
}catch(Exception ex)
{
log.error("Exception occurs:-----"+ex.getMessage(),ex);	
//ex.printStackTrace();
}
return null; 
}

public void closeConnection(MongoClient mongo)
{
if(log.isDebugEnabled()) {
log.debug("*****************************************  checked into getConnection of BaseDAOMongo  ************************************************");	
}		
try{
mongo.close();
}catch(Exception ex)
{
log.error("Exception occurs:-----"+ex.getMessage(),ex);	
//ex.printStackTrace();
}
}


public Properties getProperties() {
if(log.isDebugEnabled()) {
log.debug("*****************************************  checked into getConnection of BaseDAO  ************************************************");	
}
Properties config=new Properties();
InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
try {
config.load(input);	
}catch(Exception ex) {
log.error("Exception occurs:-----"+ex.getMessage(),ex);	
//ex.printStackTrace();
}

return config;
}

public String readFromFile(String path){
try {	
if(log.isDebugEnabled()) {
log.debug("*****************************************  checked into readFromFile of BaseDAO  ************************************************");	
}
BufferedReader br = new BufferedReader(new FileReader(new File(path)));
String read="";
String key="";
while((read=br.readLine())!=null) {
key+=read;
}
br.close();
return key;
}catch(Exception ex) {
log.error("Exception occurs:-----"+ex.getMessage(),ex);	
//ex.printStackTrace();	
}
return null;
}

public PublicKey getPublicKey(String base64PublicKey){
PublicKey publicKey = null;
try{
if(log.isDebugEnabled()) {
log.debug("*****************************************  checked into getPublicKey of BaseDAO  ************************************************");	
}	
X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
KeyFactory keyFactory = KeyFactory.getInstance("RSA");
publicKey = keyFactory.generatePublic(keySpec);
return publicKey;
} catch (NoSuchAlgorithmException ex) {
log.error("Exception occurs:-----"+ex.getMessage(),ex);	
//ex..printStackTrace();
} catch (InvalidKeySpecException ex) {
log.error("Exception occurs:-----"+ex.getMessage(),ex);	
//ex..printStackTrace();
}
return publicKey;
}

public PrivateKey getPrivateKey(String base64PrivateKey){
if(log.isDebugEnabled()) {
log.debug("*****************************************  checked into getPrivateKey of BaseDAO  ************************************************");	
}
PrivateKey privateKey = null;
PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
KeyFactory keyFactory = null;
try {
keyFactory = KeyFactory.getInstance("RSA");
} catch (NoSuchAlgorithmException ex) {
log.error("Exception occurs:-----"+ex.getMessage(),ex);	
//ex..printStackTrace();
}
try {
privateKey = keyFactory.generatePrivate(keySpec);
} catch (InvalidKeySpecException ex) {
log.error("Exception occurs:-----"+ex.getMessage(),ex);	
//ex..printStackTrace();
}
return privateKey;
}

public byte[] encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
if(log.isDebugEnabled()) {
log.debug("*****************************************  checked into encrypt of BaseDAO  ************************************************");	
}
Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
return cipher.doFinal(data.getBytes());
}

public String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
if(log.isDebugEnabled()) {
log.debug("*****************************************  checked into decrypt of BaseDAO  ************************************************");	
}
Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
cipher.init(Cipher.DECRYPT_MODE, privateKey);
return new String(cipher.doFinal(data));
}

public String decrypt(String data, String base64PrivateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
if(log.isDebugEnabled()) {
log.debug("*****************************************  checked into decrypt of BaseDAO  ************************************************");	
}
return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
}

public long getLicense(){
try {
if(log.isDebugEnabled()) {
log.debug("*****************************************  checked into getLicense of BaseDAO  ************************************************");	
}	
Properties config=getProperties();	
//String publicKey=readFromFile(config.getProperty("license.public.key"));
String privateKey=readFromFile(config.getProperty("license.private.key"));
SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
String currentDate=simpleDateFormat.format(new Date());
String encryptedString=readFromFile(config.getProperty("license.key"));
String decryptedString = decrypt(encryptedString, privateKey);
Date dateCurrent = simpleDateFormat.parse(currentDate);
Date dateLicense = simpleDateFormat.parse(decryptedString);
long difference_In_Days=((dateLicense.getTime()-dateCurrent.getTime())/(1000 * 60 * 60 * 24))%365;
return difference_In_Days;
} catch (Exception ex) {
log.error("Exception occurs:-----"+ex.getMessage(),ex);	
//ex.printStackTrace();
}
return 0;
}


}
