package com.liveQIQI.config;

import com.liveQIQI.websocket.client.ClientSocket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.websocket.*;

/**
 * 暂时没用到
 */
@Configuration
public class WebSocketClientConfig extends ClientEndpointConfig.Configurator {

//    @Bean
//    public WebSocketContainer webSocketContainer(){
//        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//        container.setDefaultMaxSessionIdleTimeout(30000L);
//        return container;
//    }

}
