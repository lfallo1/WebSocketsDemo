package guru.springframework.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private SimpMessagingTemplate messageTemplate;

    private Set<String> mySet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    private Map<String, Set<String>> channelCount = new HashMap<>();

    @EventListener
    private void onSessionConnectedEvent(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());

        //add sessionid
        mySet.add(sha.getSessionId());
    }

    @EventListener
    private void onSessionDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());

        //remove sessionid from set & remove participant from all individual channels
        mySet.remove(sha.getSessionId());
        removeParticipantFromChannels(event);
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        System.out.println("handleSessionSubscribeEvent");

        //add as a participant to channel
        addParticipant(event);
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        System.out.println("handleSessionUnsubscribeEvent");
        String channel = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER, String.class);
        //remove participant from channel
        removeParticipant(event, channel);
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        System.out.println("handleSessionDisconnectEvent");
        //remove participant from channels
        removeParticipantFromChannels(event);
    }

    /**
     * remove the session id from every channel
     *
     * @param event
     */
    private void removeParticipantFromChannels(AbstractSubProtocolEvent event) {
        for (String key : this.channelCount.keySet()) {
            String destination = "/topic/users" + key.substring(key.lastIndexOf("/"));
            removeParticipant(event, key);
        }
    }

    /**
     * remove
     *
     * @param event
     */
    private void removeParticipant(AbstractSubProtocolEvent event, String channel) {

        String sessionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);
        String destination = "/topic/users" + channel.substring(channel.lastIndexOf("/"));

        Set<String> sessionIds = this.channelCount.get(channel);
        for (String s : sessionIds) {
            if (s.equals(sessionId)) {
                sessionIds.remove(s);
                this.channelCount.put(channel, sessionIds);

                System.out.println("sending to " + destination);
                messageTemplate.convertAndSend(destination, sessionIds);
                break;
            }
        }
    }

    private void addParticipant(AbstractSubProtocolEvent event) {

        String sessionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);
        String channel = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.DESTINATION_HEADER, String.class);
        String destination = "/topic/users" + channel.substring(channel.lastIndexOf("/"));

        if (this.channelCount.get(channel) == null) {
            Set<String> sessionIds = new HashSet<>();
            sessionIds.add(sessionId);
            this.channelCount.put(channel, sessionIds);
        } else {
            Set<String> sessionIds = this.channelCount.get(channel);
            sessionIds.add(sessionId);
            this.channelCount.put(channel, sessionIds);
        }
        System.out.println("sending to " + destination);
        messageTemplate.convertAndSend(destination, this.channelCount.get(channel));
    }

}
