package guru.springframework.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lancefallon on 7/18/17.
 */
@Component
public class WebSocketEvents {

    private Map<String, Set<String>> channelCount = new HashMap<>();

    @Autowired
    private SimpMessagingTemplate messageTemplate;

    Set<String> mySet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @EventListener
    private void onSessionConnectedEvent(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        mySet.add(sha.getSessionId());
    }

    @EventListener
    private void onSessionDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        mySet.remove(sha.getSessionId());
        removeParticipantFromChannels(sha.getSessionId());
    }

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

    private void removeParticipantFromChannels(String sessionId) {
        for (String key : this.channelCount.keySet()) {
            String destination = "/topic/users" + key.substring(key.lastIndexOf("/"));
            for (String session : this.channelCount.get(key)) {
                if (session.equals(sessionId)) {
                    Set<String> sessionIds = this.channelCount.get(key);
                    sessionIds.remove(session);
                    this.channelCount.put(key, sessionIds);
                    messageTemplate.convertAndSend(destination, sessionIds);
//                    return;
                }
            }
        }
    }

    private void addParticipant(String channel, String sessionId) {
        if (this.channelCount.get(channel) == null) {
            Set<String> sessionIds = new HashSet<>();
            sessionIds.add(sessionId);
            this.channelCount.put(channel, sessionIds);
        } else {
            Set<String> sessionIds = this.channelCount.get(channel);
            sessionIds.add(sessionId);
            this.channelCount.put(channel, sessionIds);
        }
    }

    private void removeParticipant(String channel, String sessionId) {
        Set<String> sessionIds = this.channelCount.get(channel);
        for (String s : sessionIds) {
            if (s.equals(sessionId)) {
                sessionIds.remove(s);
            }
        }
        this.channelCount.put(channel, sessionIds);
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {

        System.out.println("handleSessionSubscribeEvent");

        String sessionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);
        String channel = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.DESTINATION_HEADER, String.class);
        String destination = "/topic/users" + channel.substring(channel.lastIndexOf("/"));
        addParticipant(channel, sessionId);

        messageTemplate.convertAndSend(destination, this.channelCount.get(channel));
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        System.out.println("handleSessionUnsubscribeEvent");

        String sessionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);
        String channel = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER, String.class);
        String destination = "/topic/users" + channel.substring(channel.lastIndexOf("/"));
        removeParticipant(channel, sessionId);

        messageTemplate.convertAndSend(destination, this.channelCount.get(channel));
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        System.out.println("handleSessionDisconnectEvent");
        String sessionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);
        String channel = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER, String.class);
        removeParticipant(channel, sessionId);
    }

}
