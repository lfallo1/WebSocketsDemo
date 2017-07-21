package com.lancefallon.websocket;

/**
 * Created by lfallon on 7/17/2017.
 */
public class OutputMessage {
    private String from;
    private String channel;
    private String text;
    private String time;

    public OutputMessage(final String from, final String text, final String time, final String channel) {

        this.from = from;
        this.channel = channel;
        this.text = text;
        this.time = time;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
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
}
