package org.xplorg.tool.telco360.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService extends BaseDAOMongo{

Logger log = LogManager.getLogger(WebSocketService.class.getName());	

@Autowired
public SimpMessageSendingOperations messagingTemplate;

public void sendMessage(String userId, String message ) {
if (log.isDebugEnabled()) {
log.debug("*****************************************  checked into sendMessage of WebSocketService  ************************************************");
}	
messagingTemplate.convertAndSend( "/topic/telco360/"+userId, new String(message) );
}
}
