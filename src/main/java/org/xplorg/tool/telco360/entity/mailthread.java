package org.xplorg.tool.telco360.entity;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xplorg.tool.telco360.config.BaseDAOMongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

public class mailthread extends BaseDAOMongo{

Logger log = LogManager.getLogger(mailthread.class.getName());

String userid,area,subject,content,file_attach;

public mailthread(String userid,String area,String subject,String content,String file_attach) {
this.userid=userid;
this.area=area;
this.subject=subject;
this.content=content;
this.file_attach=file_attach;
}

public void mail(){
if(log.isDebugEnabled()) {	
log.debug("*************** checked into mail ****************");	
}
	
try {
Properties config=new Properties();
InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");

config.load(input);	
ArrayList<String>to=new ArrayList<String>();
ArrayList<String>contact=new ArrayList<String>();
String tableName="trouble_ticket_details";
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<Document> resultSet = collection.find(and(eq("area",area),eq("user_id",userid))).into(new ArrayList<Document>());
JSONArray jsonArray = new JSONArray(JSON.serialize(resultSet));

for(int i=0;i<jsonArray.length();i++) {
JSONObject jsonObject = jsonArray.getJSONObject(i);
to.add(jsonObject.getString("email_id"));
if(jsonObject.getString("contact_no").trim().length()>0) {
contact.add(jsonObject.getString("contact_no").trim());	
}
}
Properties props = new Properties();
String from =  "";



props.put("mail.smtp.host", config.getProperty("mail.smtp.host"));
props.put("mail.stmp.user",  config.getProperty("mail.stmp.user"));
//If you want you use TLS
props.put("mail.smtp.auth",  config.getProperty("mail.smtp.auth"));

props.put("mail.smtp.starttls.enable",  config.getProperty("mail.smtp.starttls.enable"));
props.put("mail.smtp.password",  config.getProperty("mail.smtp.password"));
// If you want to use SSL
props.put("mail.smtp.socketFactory.port",  config.getProperty("mail.smtp.socketFactory.port"));
props.put("mail.smtp.socketFactory.class", config.getProperty("mail.smtp.socketFactory.class"));
props.put("mail.smtp.auth",  config.getProperty("mail.smtp.auth"));
props.put("mail.smtp.port",  config.getProperty("mail.smtp.port"));


/*
MongoCollection<Document> collectionEmail = database.getCollection("emailproperties");


Map < String, Object > groupMap = new HashMap < String, Object > ();
groupMap.put("parameter", "$parameter");
groupMap.put("value", "$value");
DBObject groupFields = new BasicDBObject(groupMap);

ArrayList < Document > iterDo = collectionEmail.aggregate(Arrays.asList(group(groupFields))).into(new ArrayList < Document > ());
JSONArray jsonArray1 = new JSONArray(JSON.serialize(iterDo));

for (int i = 0; i < jsonArray1.length(); i++) {
JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
JSONObject jsonObject_id = jsonObject1.getJSONObject("_id");

String parameter=jsonObject_id.optString("parameter").trim();
String value=jsonObject_id.optString("value").trim();

try {

if(parameter.equals("from")) {
from=value;
}

if(parameter.equals("mail.smtp.host")) {
props.setProperty(parameter, value);
}

else if(parameter.equals("mail.smtp.ssl.trust")) {
props.setProperty(parameter, value);
}
else if(parameter.equals("mail.smtp.port")) {
props.put(parameter, Integer.parseInt(value));
}
else {
props.put(parameter, value);
}
	
} catch (Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
ex.printStackTrace();
	
}   

}
*/
closeConnection(mongo);
Session session = Session.getDefaultInstance(props);

MimeMessage msg = new MimeMessage(session);
msg.setFrom(new InternetAddress(from));
InternetAddress[] addressTo = new InternetAddress[to.size()];
for (int i = 0; i < to.size(); i++)
{
addressTo[i] = new InternetAddress(to.get(i));
}
msg.setRecipients(RecipientType.TO, addressTo);
msg.setSubject(subject);
// Create the message part
BodyPart messageBodyPart = new MimeBodyPart();
// Fill the message
messageBodyPart.setText(content);
// Create a multipart message
Multipart multipart = new MimeMultipart();

//Set text message part
multipart.addBodyPart(messageBodyPart);

if(file_attach.length()>0) {
MimeBodyPart attachmentPart = new MimeBodyPart();
attachmentPart.attachFile(new File(file_attach));
multipart.addBodyPart(attachmentPart);
//msg.setContent(multipart);
}

// Send the complete message parts
msg.setContent(multipart );
Transport.send(msg);
//new Thread(new watsappthread(contact,txt.replaceAll("/", "\\"))).start();
System.out.println("E-mail sent !");
}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);
ex.printStackTrace();
}
}

}