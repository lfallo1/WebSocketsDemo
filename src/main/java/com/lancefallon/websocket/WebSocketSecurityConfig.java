package com.lancefallon.websocket;

import com.lancefallon.websocket.services.WebSocketSendMessageInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import static org.springframework.messaging.simp.SimpMessageType.MESSAGE;
import static org.springframework.messaging.simp.SimpMessageType.SUBSCRIBE;

/**
 * Created by lancefallon on 7/17/17.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        //anyone can subscribe, only authenticated users may send messages
        messages
                .simpTypeMatchers(SUBSCRIBE).permitAll()
                .simpTypeMatchers(MESSAGE).authenticated();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/shared", "/channelcount", "/direct");
        registry.addEndpoint("/shared", "/channelcount", "/direct").withSockJS();
    }

    @Override
    public void customizeClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(new WebSocketSendMessageInterceptor());
    }

}
