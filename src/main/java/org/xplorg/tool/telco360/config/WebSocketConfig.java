package org.xplorg.tool.telco360.config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Configuration
@EnableWebSocketMessageBroker
@ComponentScan("org.xplorg.tool.telco360")

public class WebSocketConfig extends BaseDAOMongo implements WebSocketMessageBrokerConfigurer {

Logger log = LogManager.getLogger(WebSocketConfig.class.getName());

public void configureMessageBroker(MessageBrokerRegistry config) {
if (log.isDebugEnabled()) {
log.debug("*****************************************  checked into configureMessageBroker of WebSocketConfig  ************************************************");
}
config.enableSimpleBroker("/topic");
config.setApplicationDestinationPrefixes("/app");
}


//End Point 
public void registerStompEndpoints(StompEndpointRegistry registry) {
if (log.isDebugEnabled()) {
log.debug("*****************************************  checked into registerStompEndpoints of WebSocketConfig  ************************************************");
}
registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:4200").withSockJS();
}
    
@EventListener(SessionConnectEvent.class)
public void handleWebsocketConnectListner(SessionConnectEvent event) {
try {
if (log.isDebugEnabled()) {
log.debug("*****************************************  checked into handleWebsocketConnectListner of WebSocketConfig  ************************************************");
}
StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
String id=sha.getLogin();
String sid=sha.getSessionId();

System.out.println(id+"<===>"+sid);

userIds.add(id);
sessionIds.add(sid);
userSubscription.add("-");
userSshSubscription.add("-");
//System.out.println("userIds<===>"+userIds);

/*
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("admin_detail");
SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String date=sdf.format(new Date());
UpdateResult updateResult = collection.updateMany(and(eq("admin_id",id)),Updates.combine(Updates.set("status", "yes"),Updates.combine(Updates.set("sessionid",sid)),Updates.set("lastupdate",date)));
int ret=(int) updateResult.getModifiedCount();
closeConnection(mongo);
*/
}catch(Exception ex) {
log.error("Exception occurs:-----" + ex.getMessage(), ex);
//ex.printStackTrace();	
}
}

@EventListener(SessionDisconnectEvent.class)
public void handleWebsocketDisconnectListner(SessionDisconnectEvent event) {
try {
if (log.isDebugEnabled()) {
log.debug("*****************************************  checked into handleWebsocketDisconnectListner of WebSocketConfig  ************************************************");
}
StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
String sid=sha.getSessionId();
String user_id="";
for(int i=0;i<sessionIds.size();i++) {
if(sessionIds.get(i).endsWith(sid)) {
sessionIds.remove(i);
user_id=userIds.get(i);
userIds.remove(i);	
userSubscription.remove(i);
userSshSubscription.remove(i);
}
}
/*
Properties config=getProperties();
MongoClient mongo=getConnection();
MongoDatabase database = mongo.getDatabase(config.getProperty("mongo.db.database.topology"));
MongoCollection<Document> collection = database.getCollection("admin_detail");

UpdateResult updateResult = collection.updateMany(and(eq("sessionid",sid)),Updates.combine(Updates.set("status", "no"),Updates.set("sessionid","-")));
int ret=(int) updateResult.getModifiedCount();
closeConnection(mongo);
*/
if(log.isInfoEnabled()) {	
log.info("User Id:- "+user_id+" has been Logged out from Telco360.");		
}

}catch(Exception ex) {
log.error("Exception occurs:-----" + ex.getMessage(), ex);
//ex.printStackTrace();	
}
}
}