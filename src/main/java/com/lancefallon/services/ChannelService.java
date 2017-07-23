package com.lancefallon.services;

import com.lancefallon.config.security.CustomUserDetails;
import com.lancefallon.config.security.CustomUsernamePasswordAuthenticationToken;
import com.lancefallon.domain.Channel;
import com.lancefallon.domain.Transcriber;
import com.lancefallon.repositories.ChannelRepository;
import com.lancefallon.repositories.TranscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Service
public class ChannelService {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private TranscriberRepository transcriberRepository;

    public List<Channel> findAll(Principal auth) {

        List<Channel> channels = this.channelRepository.findAll();

        if (auth != null) {
            CustomUsernamePasswordAuthenticationToken token = (CustomUsernamePasswordAuthenticationToken) auth;
            CustomUserDetails user = (CustomUserDetails) token.getPrincipal();
            if (user.getAuthorities().stream().filter(a -> a.getAuthority().toLowerCase().equals("role_transcriber")).findAny().isPresent()) {
                List<Channel> transcriberChannels = this.channelRepository.findByTranscriber(auth.getName());
                for (Channel channel : channels) {
                    for (Channel transcriberChannel : transcriberChannels) {
                        if (channel.getChannelId().equals(transcriberChannel.getChannelId())) {
                            Transcriber transcriber = new Transcriber();
                            transcriber.setUser(user);
                            channel.setTranscribers(Arrays.asList(transcriber));
                        }
                    }
                }
            }
        }

        return channels;
    }

    public List<Channel> findAllWithTranscribers() {
        List<Channel> channels = this.channelRepository.findAll();
        for (Channel channel : channels) {
            channel.setTranscribers(this.transcriberRepository.findByChannel(channel.getChannelId()));
        }
        return channels;
    }

}
