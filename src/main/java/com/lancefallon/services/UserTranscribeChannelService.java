package com.lancefallon.services;

import com.lancefallon.domain.UserTranscribeChannel;
import com.lancefallon.repositories.UserTranscribeChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserTranscribeChannelService {

    @Autowired
    private UserTranscribeChannelRepository userTranscribeChannelRepository;

    public UserTranscribeChannel findByUsername(String username) {
        return this.userTranscribeChannelRepository.getUserTranscribeChannelByUser(username);
    }

}