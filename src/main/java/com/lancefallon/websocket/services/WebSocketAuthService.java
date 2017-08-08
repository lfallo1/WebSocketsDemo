package com.lancefallon.websocket.services;

import com.lancefallon.config.AppProperties;
import com.lancefallon.config.exception.UnauthorizedException;
import com.lancefallon.config.security.CustomUserDetails;
import com.lancefallon.config.security.CustomUsernamePasswordAuthenticationToken;
import com.lancefallon.domain.Channel;
import com.lancefallon.services.TranscriberService;
import com.lancefallon.websocket.models.ChannelSubscription;
import com.lancefallon.websocket.models.IsTranscriberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * handle authentication at method level for messages being sent
 */
@Service
public class WebSocketAuthService {

    @Autowired
    private TranscriberService transcriberService;

    @Autowired
    private AppProperties appProperties;

    public boolean validateCanSend(Principal auth, String channel) {
        Optional<Channel> channelTranscriber = this.appProperties.getChannels().stream()
                .filter(c -> c.getTranscribers().stream()
                        .filter(t -> t.getUser().getUsername().equals(auth.getName()))
                        .collect(Collectors.toList()).size() > 0 && String.valueOf(c.getChannelId()).equals(channel))
                .findFirst();

        CustomUserDetails user = ((CustomUserDetails) ((CustomUsernamePasswordAuthenticationToken) auth).getPrincipal());
        return user.isTranscribing() && channelTranscriber.isPresent();
    }

    public IsTranscriberDto authenticateSubscriber(String channel, Map<String, Set<ChannelSubscription>> channelSubscribers, Principal auth) throws UnauthorizedException {

        Boolean isTranscribing = false;
        Boolean isTranscriberOfChannel = false;

        if (auth != null && this.transcriberService.isTranscriberOfChannel(auth, channel.substring(channel.lastIndexOf("/") + 1))) {
            isTranscriberOfChannel = true;
            CustomUsernamePasswordAuthenticationToken token = (CustomUsernamePasswordAuthenticationToken) auth;
            CustomUserDetails user = (CustomUserDetails) token.getPrincipal();
            isTranscribing = true;
            user.setTranscribing(isTranscribing);

            //next check if a transcriber already exists on the channel
            if (channelSubscribers.get(channel) != null) {

                //check if another user is already transcribing
                Optional<ChannelSubscription> existingTranscriber = channelSubscribers.get(channel).stream()
                        .filter(s -> s.getTranscriber() && !s.getUser().getName().equals(auth.getName()))
                        .findFirst();
                if (existingTranscriber.isPresent()) {
                    isTranscribing = false;
                    user.setTranscribing(isTranscribing);
                }
            }
            token = new CustomUsernamePasswordAuthenticationToken(user, token.getCredentials());
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        return new IsTranscriberDto(isTranscribing, isTranscriberOfChannel);
    }

}
