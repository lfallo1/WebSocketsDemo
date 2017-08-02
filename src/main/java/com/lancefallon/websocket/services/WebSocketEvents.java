package com.lancefallon.websocket.services;

import com.lancefallon.config.exception.UnauthorizedException;
import com.lancefallon.websocket.models.ChannelSubscription;
import com.lancefallon.websocket.models.IsTranscriberDto;
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

    //store subscriber / session info
    private Map<String, Set<ChannelSubscription>> channelSubscribers = new HashMap<>();
    private Set<String> users = new HashSet<>();

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) throws UnauthorizedException {

        if (event.getUser() != null) {
            users.add(event.getUser().getName());
            System.out.println(String.format("total connected users %d", this.users.size()));
            messageTemplate.convertAndSend("/topic/users/connected", this.users);
        }

        if (!event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.DESTINATION_HEADER, String.class).contains("direct")) {
            //add as a participant to channel
            addParticipant(event);
        }
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        String sessionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER, String.class);
        String subscriptionId = event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER, String.class);
        //remove participant from channel
        removeParticipantBySubscriptionId(subscriptionId, sessionId);
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {

        if (event.getUser() != null) {
            users.remove(event.getUser().getName());
            System.out.println(String.format("total connected users %d", this.users.size()));
            messageTemplate.convertAndSend("/topic/users/connected", this.users);
            messageTemplate.convertAndSend("/topic/users/disconnect", event.getUser().getName());
        }

        //remove participant from channels
        removeParticipantBySessionId(event);
    }

    /**
     * removes a participant by their subscriber id.
     * there will only be one subscriber id
     *
     * @param subscriptionId
     */
    private void removeParticipantBySubscriptionId(String subscriptionId, String sessionId) {

        for (String channel : this.channelSubscribers.keySet()) {
            Set<ChannelSubscription> subscriptions = this.channelSubscribers.get(channel);
            for (ChannelSubscription subscription : subscriptions) {
                if (subscription.getSubscriptionId().equals(subscriptionId) && subscription.getSessionId().equals(sessionId)) {
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
        Boolean isAuthenticatedToTranscribe = false;
        if (auth != null) {
            IsTranscriberDto authValues = this.webSocketAuthService.authenticateSubscriber(channel, this.channelSubscribers, auth);

            //this is strange, but essentially only one user can transcribe on a channel at once.
            //if multiple users that are capable / allowed to transcribe on a channel login simultaneously, then only the one
            //will be able to transcribe.  these helper values are used on the front to display useful message(s) to users
            //so they understand instances where they are logged in but cannot transcribe
            isTranscriber = authValues.getCurrentTranscriber();
            isAuthenticatedToTranscribe = authValues.getTranscriberOfChannel();
        }
        ChannelSubscription channelSubscription = new ChannelSubscription(sessionId, subscriptionId, auth, isTranscriber, isAuthenticatedToTranscribe);

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
