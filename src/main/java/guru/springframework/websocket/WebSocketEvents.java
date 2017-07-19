package guru.springframework.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lancefallon on 7/18/17.
 */
@Component
public class WebSocketEvents {

    private Map<String, Integer> channelCount = new HashMap<>();

    @Autowired
    private SimpMessagingTemplate messageTemplate;

    @EventListener
    public void handleBrokerAvailabilityEvent(BrokerAvailabilityEvent event) {
        System.out.println("handleBrokerAvailabilityEvent");
    }

    @EventListener
    public void handleSessionConnectEvent(SessionConnectEvent event) {
        System.out.println("handleSessionConnectEvent");
    }

    @EventListener
    public void handleSessionConnectedEvent(SessionConnectedEvent event) {
        System.out.println("handleSessionConnectedEvent");
    }

    private void addParticipant(String channel) {
        if (this.channelCount.get(channel) == null) {
            this.channelCount.put(channel, 1);
        } else {
            this.channelCount.put(channel, this.channelCount.get(channel) + 1);
        }
    }

    private void removeParticipant(String channel) {
        this.channelCount.put(channel, this.channelCount.get(channel) - 1);
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {

        System.out.println("handleSessionSubscribeEvent");

        String channel = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.DESTINATION_HEADER, String.class);
        String destination = "/topic/users" + channel.substring(channel.lastIndexOf("/"));
        addParticipant(channel);

        messageTemplate.convertAndSend(destination, this.channelCount.get(channel));
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        System.out.println("handleSessionUnsubscribeEvent");

        String channel = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER, String.class);
        String destination = "/topic/users" + channel.substring(channel.lastIndexOf("/"));
        removeParticipant(channel);

        messageTemplate.convertAndSend(destination, this.channelCount.get(channel));
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        System.out.println("handleSessionDisconnectEvent");
    }

}
