package com.lancefallon.websocket.models;

/**
 * IsTranscriberDto - helper pojo holding values related to a transcriber's current status in a channel
 */
public class IsTranscriberDto {

    //whether or not is currently transcribing on a channel
    private Boolean isCurrentTranscriber;

    //if a designated transcriber of channel (matches the database value, and is NOT necessarily the current transcriber)
    private Boolean isTranscriberOfChannel;

    public IsTranscriberDto() {
    }

    public IsTranscriberDto(Boolean isCurrentTranscriber, Boolean isTranscriberOfChannel) {
        this.isCurrentTranscriber = isCurrentTranscriber;
        this.isTranscriberOfChannel = isTranscriberOfChannel;
    }

    public Boolean getCurrentTranscriber() {
        return isCurrentTranscriber;
    }

    public void setCurrentTranscriber(Boolean currentTranscriber) {
        isCurrentTranscriber = currentTranscriber;
    }

    public Boolean getTranscriberOfChannel() {
        return isTranscriberOfChannel;
    }

    public void setTranscriberOfChannel(Boolean transcriberOfChannel) {
        isTranscriberOfChannel = transcriberOfChannel;
    }
}
