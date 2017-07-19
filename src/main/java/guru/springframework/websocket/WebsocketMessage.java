package guru.springframework.websocket;

/**
 * Created by lfallon on 7/17/2017.
 */
public class WebsocketMessage {
    private String from;
    private String text;

    public String getText() {
        return text;
    }

    public String getFrom() {
        return from;
    }
}
