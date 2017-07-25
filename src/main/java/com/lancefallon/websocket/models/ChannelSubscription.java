package com.lancefallon.websocket.models;

import java.security.Principal;

public class ChannelSubscription {
    private String sessionId;
    private String subscriptionId;
    private Principal user;
    private Boolean isTranscriber;
    private Boolean isAuthenticatedToTranscribe;

    public ChannelSubscription(String sessionId, String subscriptionId, Principal user, Boolean isTranscriber, Boolean isAuthenticatedToTranscribe) {
        this.sessionId = sessionId;
        this.subscriptionId = subscriptionId;
        this.user = user;
        this.isTranscriber = isTranscriber;
        this.isAuthenticatedToTranscribe = isAuthenticatedToTranscribe;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Principal getUser() {
        return user;
    }

    public void setUser(Principal user) {
        this.user = user;
    }

    public Boolean getTranscriber() {
        return isTranscriber;
    }

    public void setTranscriber(Boolean transcriber) {
        isTranscriber = transcriber;
    }

    public Boolean getAuthenticatedToTranscribe() {
        return isAuthenticatedToTranscribe;
    }

    public void setAuthenticatedToTranscribe(Boolean authenticatedToTranscribe) {
        isAuthenticatedToTranscribe = authenticatedToTranscribe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChannelSubscription that = (ChannelSubscription) o;

        if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null) return false;
        return subscriptionId != null ? subscriptionId.equals(that.subscriptionId) : that.subscriptionId == null;
    }

    @Override
    public int hashCode() {
        int result = sessionId != null ? sessionId.hashCode() : 0;
        result = 31 * result + (subscriptionId != null ? subscriptionId.hashCode() : 0);
        return result;
    }
}
