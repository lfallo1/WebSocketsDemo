package com.lancefallon.config.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

/**
 * listen for session destroyed events (either from logging out or session expiring)
 */
@Component
public class SessionExpirationListener implements ApplicationListener<SessionDestroyedEvent> {

    /**
     * handle session destroyed event
     * @param event
     */
    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        //TODO perhaps send message to front (wont be difficult to handle on front, but still might be overkill, so leaving out for now)
    }

}