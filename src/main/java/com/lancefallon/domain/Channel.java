package com.lancefallon.domain;

public class Channel {

    private Integer channelId;
    private String name;

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
}
