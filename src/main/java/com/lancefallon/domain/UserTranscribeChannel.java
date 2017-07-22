package com.lancefallon.domain;

import com.lancefallon.config.security.CustomUserDetails;

public class UserTranscribeChannel {

    private Integer userTranscribeId;
    private CustomUserDetails user;
    private Channel channel;

    public UserTranscribeChannel() {
    }

    public UserTranscribeChannel(Integer userTranscribeId, CustomUserDetails user, Channel channel) {
        this.userTranscribeId = userTranscribeId;
        this.user = user;
        this.channel = channel;
    }

    public Integer getUserTranscribeId() {
        return userTranscribeId;
    }

    public void setUserTranscribeId(Integer userTranscribeId) {
        this.userTranscribeId = userTranscribeId;
    }

    public CustomUserDetails getUser() {
        return user;
    }

    public void setUser(CustomUserDetails user) {
        this.user = user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
