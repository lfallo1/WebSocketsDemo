package com.lancefallon.domain;

import com.lancefallon.config.security.CustomUserDetails;

public class Transcriber {

    private Integer transcribeId;
    private CustomUserDetails user;
    private Channel channel;

    public Transcriber() {
    }

    public Transcriber(Integer transcribeId, CustomUserDetails user, Channel channel) {
        this.transcribeId = transcribeId;
        this.user = user;
        this.channel = channel;
    }

    public Integer getTranscribeId() {
        return transcribeId;
    }

    public void setTranscribeId(Integer transcribeId) {
        this.transcribeId = transcribeId;
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
