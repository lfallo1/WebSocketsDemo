package com.lancefallon.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lancefallon on 7/18/17.
 */
@Component
public class WebSocketEvents {

    @Autowired
    private SimpMessagingTemplate messageTemplate;

    //store subscriber / session info
    private Map<String, Set<ChannelSubscription>> channelCount = new HashMap<>();

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        System.out.println("handleSessionSubscribeEvent");

        //add as a participant to channel
        addParticipant(event);
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        System.out.println("handleSessionUnsubscribeEvent");
        String subscriptionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER, String.class);
        //remove participant from channel
        removeParticipantBySubscriptionId(subscriptionId);
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        System.out.println("handleSessionDisconnectEvent");
        //remove participant from channels
        removeParticipantBySessionId(event);
    }

    /**
     * removes a participant by their subscriber id.
     * there will only be one subscriber id
     *
     * @param subscriptionId
     */
    private void removeParticipantBySubscriptionId(String subscriptionId) {

        for (String channel : this.channelCount.keySet()) {
            Set<ChannelSubscription> subscriptions = this.channelCount.get(channel);
            for (ChannelSubscription subscription : subscriptions) {
                if (subscription.getSubscriptionId().equals(subscriptionId)) {
                    subscriptions.remove(subscription);
                    this.channelCount.put(channel, subscriptions);

                    String destination = "/topic/users" + channel.substring(channel.lastIndexOf("/"));
                    System.out.println("sending to " + destination);
                    messageTemplate.convertAndSend(destination, subscriptions);
                    break;
                }
            }
        }
    }

    /**
     * remove participants by their session id.
     * they may be subscribed to multiple channels with the same session id, so need to check all
     *
     * @param event
     */
    private void removeParticipantBySessionId(AbstractSubProtocolEvent event) {
        for (String channel : this.channelCount.keySet()) {

            String sessionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);
            String destination = "/topic/users" + channel.substring(channel.lastIndexOf("/"));

            Set<ChannelSubscription> subscriptions = this.channelCount.get(channel);
            for (ChannelSubscription subscription : subscriptions) {
                if (subscription.getSessionId().equals(sessionId)) {
                    subscriptions.remove(subscription);
                    this.channelCount.put(channel, subscriptions);

                    System.out.println("sending to " + destination);
                    messageTemplate.convertAndSend(destination, subscriptions);
                    break;
                }
            }
        }
    }

    /**
     * adds a participant
     *
     * @param event
     */
    private void addParticipant(AbstractSubProtocolEvent event) {

        String subscriptionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER, String.class);
        String sessionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);
        String channel = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.DESTINATION_HEADER, String.class);
        String destination = "/topic/users" + channel.substring(channel.lastIndexOf("/"));

        if (this.channelCount.get(channel) == null) {
            Set<ChannelSubscription> subscriptions = new HashSet<>();
            subscriptions.add(new ChannelSubscription(sessionId, subscriptionId));
            this.channelCount.put(channel, subscriptions);
        } else {
            Set<ChannelSubscription> subscriptions = this.channelCount.get(channel);
            subscriptions.add(new ChannelSubscription(sessionId, subscriptionId));
            this.channelCount.put(channel, subscriptions);
        }
        System.out.println("sending to " + destination);
        messageTemplate.convertAndSend(destination, this.channelCount.get(channel));
    }

}
