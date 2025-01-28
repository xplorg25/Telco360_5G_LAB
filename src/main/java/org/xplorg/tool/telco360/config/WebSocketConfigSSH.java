package org.xplorg.tool.telco360.config;

import org.springframework.web.socket.config.annotation.*;
import org.springframework.context.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Configuration
@EnableWebSocket
public class WebSocketConfigSSH implements WebSocketConfigurer {
	
	 private static final Logger log = LogManager.getLogger(WebSocketConfigSSH.class);

	    @Override
	    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
	        log.debug("Registering custom WebSocket handler for /ssh.");
	        registry.addHandler(new SshWebsocketHandler(), "/ssh")
	                .setAllowedOrigins("http://localhost:4200");
	    }

}
