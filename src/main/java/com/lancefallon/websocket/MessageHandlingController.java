package com.lancefallon.websocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class MessageHandlingController {

    @MessageMapping("/shared/{channel}")
    @SendTo("/topic/transcription/{channel}")
    public OutputMessage send(MessageHeaderAccessor header, @DestinationVariable String channel, WebsocketMessage websocketMessage) throws AccessDeniedException {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(websocketMessage.getFrom(), websocketMessage.getText(), time, channel);
    }

    @MessageMapping("/direct/request/{username}")
    @SendTo("/topic/direct/request/{username}")
    public OutputMessage requestDirect(@DestinationVariable String username, WebsocketMessage websocketMessage) throws AccessDeniedException {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(websocketMessage.getFrom(), websocketMessage.getText(), time, username);
    }

    @MessageMapping("/direct/message/{uniqueChannel}")
    @SendTo("/topic/direct/message/{uniqueChannel}")
    public OutputMessage sendDirect(@DestinationVariable String uniqueChannel, WebsocketMessage websocketMessage) throws AccessDeniedException {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(websocketMessage.getFrom(), websocketMessage.getText(), time, uniqueChannel);
    }

    @MessageExceptionHandler
    @SendToUser(value = "/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
}
