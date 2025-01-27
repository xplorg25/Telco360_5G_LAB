package org.xplorg.tool.telco360.config;

import org.springframework.web.socket.config.annotation.*;
import org.springframework.context.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfigSSH implements WebSocketConfigurer {
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registery) {
		registery.addHandler(new SshWebsocketHandler(), "/ssh").setAllowedOrigins("*");
	}

}
