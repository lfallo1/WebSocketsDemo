package com.lancefallon.websocket;

import com.lancefallon.config.exception.UnauthorizedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class WebSocketAuthService {

    /**
     * authenticate subscriber can transcribe (this is only true if user is authenticated, has access to requested channel, and a transcriber does not already exist)
     *
     * @param channelSubscribers
     */
    public void authenticateSubscriber(String channel, Map<String, Set<ChannelSubscription>> channelSubscribers, ChannelSubscription channelSubscription, Principal auth) throws UnauthorizedException {
        //TODO still deciding whether to throw error, or just flip the transcriber flag on the ChannelSubscription object
        if (auth != null && channelSubscribers.get(channel) != null) {
            Optional<ChannelSubscription> existingTranscriber = channelSubscribers.get(channel).stream()
                    .filter(s -> s.getTranscriber())
                    .findFirst();
            if (existingTranscriber.isPresent()) {
                channelSubscription.setTranscriber(false);
            }
        }
    }

}
