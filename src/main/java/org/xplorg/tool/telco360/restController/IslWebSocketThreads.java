package org.xplorg.tool.telco360.restController;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.xplorg.tool.telco360.config.BaseDAOMongo;
import org.xplorg.tool.telco360.config.WebSocketService;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class IslWebSocketThreads extends BaseDAOMongo implements Runnable{
Logger log = LogManager.getLogger(IslWebSocketThreads.class.getName());

String userId;
WebSocketService webSocketService;
int threadTime=120000;

public IslWebSocketThreads(WebSocketService webSocketService,String userId) {
this.webSocketService=webSocketService;
this.userId=userId;
}	
	
	
@Override
public void run() {
try {	
if (log.isDebugEnabled()) {
log.debug("*************** checked into run of IslWebSocketThreads ****************");
}	
while(true) {	
int idx=userIds.indexOf(userId);	
if(idx>-1 && userSubscription.get(idx).contains("ISL")) {	

// TODO Auto-generated method stub
Properties config=getProperties();	
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));

;
String messageNokia=getIslDetails(database, "Mpbn", "Nokia", "isl_status");
//webSocketService.sendMessage(userId, messageNokia);
//Thread.sleep(5000);
String messageEricsson=getIslDetails(database, "Mpbn", "Ericsson", "isl_status");
//webSocketService.sendMessage(userId, messageEricsson);
//Thread.sleep(5000);
String messageZte=getIslDetails(database, "Mpbn", "Zte", "isl_status");
//webSocketService.sendMessage(userId, messageZte);
//Thread.sleep(5000);

String messageHuawei=getIslDetails(database, "Ipran", "Huawei", "isl_status");
webSocketService.sendMessage(userId, messageHuawei);


//String messageJuniper=getIslDetails(database, "Mpbn", "Juniper", "isl_status");
//webSocketService.sendMessage(userId, messageJuniper);

String message=messageNokia+"#@@#"+messageZte+"#@@#"+messageEricsson;
webSocketService.sendMessage(userId, message);
System.out.println(message);
closeConnection(mongo);
Thread.sleep(threadTime);
}
else{
break;	
}
}
}catch(Exception ex) {
log.error("Exception occurs:----"+ex.getMessage(),ex);		
//ex.printStackTrace();	
}
}

//-------------------ISL(All Domains)------------------------------------------

public static String getIslDetails(MongoDatabase database,String domain,String vendor,String tableName) {
Date date=new Date();	
SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
sdf.setTimeZone(TimeZone.getTimeZone("Africa/Lusaka"));
String dt=sdf.format(date);

ArrayList<String>check=new ArrayList<String>();	
ArrayList<String>nename=new ArrayList<String>();	
ArrayList<String>neipaddress=new ArrayList<String>();	
ArrayList<String>popuptext=new ArrayList<String>();	

MongoCollection<Document> collection = database.getCollection(tableName);
ArrayList<Document> resultSet = collection.find(and(eq("date",dt),eq("status","yes"),eq("vendor",vendor),eq("domain",domain))).into(new ArrayList<Document>());

for(Document docs:resultSet) {
String name=docs.get("nename").toString();
String hostname=docs.get("hostname").toString();
String poptext=docs.get("popuptext").toString();
String chk=name+"<====>"+hostname+"<====>"+poptext;
if(check.indexOf(chk)<0) {	
check.add(chk);
nename.add(name);
neipaddress.add(hostname);
popuptext.add(poptext);
//System.out.println(docs);	
}
}

String isl="";

for(int i=0;i<nename.size();i++) {
isl=isl+""+nename.get(i)+"@@@"+neipaddress.get(i)+"~"+popuptext.get(i)+"@##@";	
}

String message="Domain:-"+domain+"; Vendor:-"+vendor+";IslStatus:-"+isl;
return message;
}

}
