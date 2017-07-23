package com.lancefallon.domain;

import java.util.List;

public class Channel {

    private Integer channelId;
    private String name;
    private List<Transcriber> transcribers;

    public Channel() {
    }

    public Channel(Integer channelId, String name) {
        this.channelId = channelId;
        this.name = name;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Transcriber> getTranscribers() {
        return transcribers;
    }

    public void setTranscribers(List<Transcriber> transcribers) {
        this.transcribers = transcribers;
    }
}
