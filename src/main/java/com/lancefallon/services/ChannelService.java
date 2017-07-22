package com.lancefallon.services;

import com.lancefallon.domain.Channel;
import com.lancefallon.repositories.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelService {

    @Autowired
    private ChannelRepository channelRepository;

    public List<Channel> findAll() {
        return this.channelRepository.findAll();
    }

}
