package org.xplorg.tool.telco360.entity;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
public class whatsapp {
	
Logger log = LogManager.getLogger(whatsapp.class.getName());
	
public void send_whatsapp(String code,String to,String message1) {
if(log.isDebugEnabled()) {	
log.debug("*************** checked into send_whatsapp ****************");	
}
try {
Properties config=new Properties();
InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");

config.load(input);	
	
Twilio.init(config.getProperty("whatsapp.ACCOUNT_SID"),config.getProperty("whatsapp.AUTH_TOKEN"));
Message message = Message.creator(
new com.twilio.type.PhoneNumber("whatsapp:+"+code+to+""),
new com.twilio.type.PhoneNumber("whatsapp:+"+config.getProperty("whatsapp.from")),
message1).create();
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
//ex.printStackTrace();
}

}

}
