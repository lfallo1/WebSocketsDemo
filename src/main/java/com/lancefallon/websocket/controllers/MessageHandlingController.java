package com.lancefallon.websocket.controllers;

import com.lancefallon.websocket.models.OutputMessage;
import com.lancefallon.websocket.models.WebsocketMessage;
import com.lancefallon.websocket.services.WebSocketAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class MessageHandlingController {

    @Autowired
    private WebSocketAuthService webSocketAuthService;

    @MessageMapping("/shared/{channel}")
    @SendTo("/topic/transcription/{channel}")
    @PreAuthorize("@webSocketAuthService.validateCanSend(#auth, #channel)")
    public OutputMessage send(Principal auth, MessageHeaderAccessor header, @DestinationVariable String channel, WebsocketMessage websocketMessage) throws AccessDeniedException {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(websocketMessage.getFrom(), websocketMessage.getText(), time, websocketMessage.getChannel(), websocketMessage.getColor());
    }

    @MessageMapping("/direct/request/{username}")
    @SendTo("/topic/direct/request/{username}")
    public OutputMessage requestDirect(@DestinationVariable String username, WebsocketMessage websocketMessage) throws AccessDeniedException {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(websocketMessage.getFrom(), websocketMessage.getText(), time, websocketMessage.getChannel(), websocketMessage.getColor());
    }

    @MessageMapping("/direct/message/{uniqueChannel}")
    @SendTo("/topic/direct/message/{uniqueChannel}")
    public OutputMessage sendDirect(@DestinationVariable String uniqueChannel, WebsocketMessage websocketMessage) throws AccessDeniedException {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(websocketMessage.getFrom(), websocketMessage.getText(), time, websocketMessage.getChannel(), websocketMessage.getColor());
    }

    @MessageExceptionHandler
    @SendToUser(value = "/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
}
