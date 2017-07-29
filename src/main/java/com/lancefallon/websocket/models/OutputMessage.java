package com.lancefallon.websocket.models;

import com.lancefallon.domain.Channel;

/**
 * Created by lfallon on 7/17/2017.
 */
public class OutputMessage {
    private String from;
    private Channel channel;
    private String text;
    private String time;
    private String color;

    public OutputMessage(final String from, final String text, final String time, final Channel channel, final String color) {

        this.from = from;
        this.channel = channel;
        this.text = text;
        this.time = time;
        this.color = color;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public String getFrom() {
        return from;
    }

    public String getColor() {
        return color;
    }
}
