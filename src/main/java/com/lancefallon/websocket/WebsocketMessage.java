package com.lancefallon.websocket;

import com.lancefallon.domain.Channel;

/**
 * Created by lfallon on 7/17/2017.
 */
public class WebsocketMessage {
    private String from;
    private String text;
    private Channel channel;

    public WebsocketMessage() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
