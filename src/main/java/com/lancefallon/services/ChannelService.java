package com.lancefallon.services;

import com.lancefallon.domain.Channel;
import com.lancefallon.repositories.ChannelRepository;
import com.lancefallon.repositories.TranscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelService {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private TranscriberRepository transcriberRepository;

    public List<Channel> findAll() {
        return this.channelRepository.findAll();
    }

    public List<Channel> findAllWithTranscribers() {
        List<Channel> channels = this.channelRepository.findAll();
        for (Channel channel : channels) {
            channel.setTranscribers(this.transcriberRepository.findByChannel(channel.getName()));
        }
        return channels;
    }

}
