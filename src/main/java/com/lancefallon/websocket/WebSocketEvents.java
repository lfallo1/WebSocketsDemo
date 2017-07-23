package com.lancefallon.websocket;

import com.lancefallon.config.exception.UnauthorizedException;
import com.lancefallon.services.TranscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;
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

    @Autowired
    private WebSocketAuthService webSocketAuthService;

    @Autowired
    private TranscriberService transcriberService;

    //store subscriber / session info
    private Map<String, Set<ChannelSubscription>> channelSubscribers = new HashMap<>();

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) throws UnauthorizedException {
        if (!event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.DESTINATION_HEADER, String.class).contains("direct")) {
            //add as a participant to channel
            addParticipant(event);
        }
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        String subscriptionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER, String.class);
        //remove participant from channel
        removeParticipantBySubscriptionId(subscriptionId);
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
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

        for (String channel : this.channelSubscribers.keySet()) {
            Set<ChannelSubscription> subscriptions = this.channelSubscribers.get(channel);
            for (ChannelSubscription subscription : subscriptions) {
                if (subscription.getSubscriptionId().equals(subscriptionId)) {
                    subscriptions.remove(subscription);
                    this.channelSubscribers.put(channel, subscriptions);

                    String destination = "/topic/channelcount" + channel.substring(channel.lastIndexOf("/"));
                    System.out.println("##removeParticipantBySubscriptionId## sending to " + destination);
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
        for (String channel : this.channelSubscribers.keySet()) {

            String sessionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);
            String destination = "/topic/channelcount" + channel.substring(channel.lastIndexOf("/"));

            Set<ChannelSubscription> subscriptions = this.channelSubscribers.get(channel);
            for (ChannelSubscription subscription : subscriptions) {
                if (subscription.getSessionId().equals(sessionId)) {
                    subscriptions.remove(subscription);
                    this.channelSubscribers.put(channel, subscriptions);

                    System.out.println("##removeParticipantBySessionId## sending to " + destination);
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
    private void addParticipant(AbstractSubProtocolEvent event) throws UnauthorizedException {

        String subscriptionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER, String.class);
        String sessionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);
        String channel = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.DESTINATION_HEADER, String.class);
        String destination = "/topic/channelcount" + channel.substring(channel.lastIndexOf("/"));

        Principal auth = event.getUser();
        Boolean isTranscriber = false;
        if (auth != null) {
            isTranscriber = this.transcriberService.isTranscriberOfChannel(auth, channel.substring(channel.lastIndexOf("/") + 1));
        }
        ChannelSubscription channelSubscription = new ChannelSubscription(sessionId, subscriptionId, auth, isTranscriber);
//        this.webSocketAuthService.authenticateSubscriber(channel, this.channelSubscribers, channelSubscription, auth);

        if (this.channelSubscribers.get(channel) == null) {
            Set<ChannelSubscription> subscriptions = new HashSet<>();
            subscriptions.add(channelSubscription);
            this.channelSubscribers.put(channel, subscriptions);
        } else {
            Set<ChannelSubscription> subscriptions = this.channelSubscribers.get(channel);
            subscriptions.add(channelSubscription);
            this.channelSubscribers.put(channel, subscriptions);
        }
        System.out.println("##addParticipant## sending to " + destination);
        messageTemplate.convertAndSend(destination, this.channelSubscribers.get(channel));
    }

}
