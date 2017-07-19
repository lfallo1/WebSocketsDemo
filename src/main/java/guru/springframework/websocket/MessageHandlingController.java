package guru.springframework.websocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class MessageHandlingController {

    @MessageMapping("/name/{channel}")
    @SendTo("/topic/messages/{channel}")
    public OutputMessage send(@DestinationVariable String channel, WebsocketMessage websocketMessage) throws Exception {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(websocketMessage.getFrom(), websocketMessage.getText(), time, channel);
    }

//    @MessageMapping("/users/{channel}")
//    @SendTo("/topic/users/{channel}")
//    public OutputMessage sendTotalUsers(@DestinationVariable String channel, String user) {
//        return new OutputMessage("", "", null, user);
//    }
}
