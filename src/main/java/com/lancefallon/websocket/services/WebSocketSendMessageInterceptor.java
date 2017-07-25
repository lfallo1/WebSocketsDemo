package com.lancefallon.websocket.services;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.DefaultUserDestinationResolver;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.UserDestinationMessageHandler;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.ExecutorSubscribableChannel;

import java.security.Principal;
import java.util.Iterator;
import java.util.Set;

public class WebSocketSendMessageInterceptor extends ChannelInterceptorAdapter {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // below code gets users in channel. commented out, but available if needed later
//        if (channel instanceof ExecutorSubscribableChannel) {
//            ExecutorSubscribableChannel subscribableChannel = (ExecutorSubscribableChannel) channel;
//            Set<MessageHandler> handlers = subscribableChannel.getSubscribers();
//            Iterator<MessageHandler> iterator = handlers.iterator();
//            while (iterator.hasNext()) {
//                MessageHandler handler = iterator.next();
//                if (handler instanceof UserDestinationMessageHandler) {
//                    DefaultUserDestinationResolver destinationResolver = (DefaultUserDestinationResolver) ((UserDestinationMessageHandler) handler).getUserDestinationResolver();
//                    Set<SimpUser> users = destinationResolver.getSimpUserRegistry().getUsers();
//                    System.out.println(users);
//                    break;
//                }
//            }
//        }

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.MESSAGE.equals(headerAccessor.getCommand())) {
            Principal userPrincipal = headerAccessor.getUser();
            if (!validateSubscription(userPrincipal, headerAccessor.getDestination())) {
                throw new IllegalArgumentException("No permission for this topic");
            }
        }
        return message;
    }

    private boolean validateSubscription(Principal principal, String topicDestination) {
        return true;
    }
}
