package org.xplorg.tool.telco360.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class watsappthread implements Runnable{
	
Logger log = LogManager.getLogger(watsappthread.class.getName());

ArrayList<String> contacts;
String message;

public watsappthread(ArrayList<String>contacts,String message) {
this.contacts=contacts;
this.message=message;
}

@Override
public void run() {
//TODO Auto-generated method stub
for(String contact:contacts) {	
send_whatsapp(contact,message);
}
}

public void send_whatsapp(String contact,String message) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into send_whatsapp ****************");	
}
try {
Properties config=new Properties();
InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");

config.load(input);	
	
Twilio.init(config.getProperty("whatsapp.ACCOUNT_SID"),config.getProperty("whatsapp.AUTH_TOKEN"));
Message msg = Message.creator(
new com.twilio.type.PhoneNumber("whatsapp:+"+contact+""),
new com.twilio.type.PhoneNumber("whatsapp:+"+config.getProperty("whatsapp.from")),message).create();
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}

}

}
