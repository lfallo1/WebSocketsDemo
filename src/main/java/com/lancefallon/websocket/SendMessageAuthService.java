package com.lancefallon.websocket;

import com.lancefallon.config.AppProperties;
import com.lancefallon.domain.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * handle authentication at method level for messages being sent
 */
@Service
public class SendMessageAuthService {

    @Autowired
    private AppProperties appProperties;

    public boolean validateCanSend(Principal auth, String channel) {
        Optional<Channel> channelTranscriber = this.appProperties.getChannels().stream()
                .filter(c -> c.getTranscribers().stream()
                        .filter(t -> t.getUser().getUsername().equals(auth.getName()))
                        .collect(Collectors.toList()).size() > 0 && c.getName().equals(channel))
                .findFirst();

        return channelTranscriber.isPresent();
    }

}
