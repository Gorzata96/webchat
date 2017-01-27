import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Gosia on 2017-01-16.
 */
@WebSocket
public class ChatWebSocketHandler {
    private Chat chat;
    private Chatbot chatbot;

    public ChatWebSocketHandler(Chat chat) {
        this.chat = chat;
        chatbot = new Chatbot();
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        chat.remove(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        if(message.startsWith("_username: ")){
            Pattern P = Pattern.compile("\"([^\"]*)\"");
            Matcher m = P.matcher(message);
            if(m.find()) {
                chat.joinChat(user, m.group(1));
            }
        }
        else if(message.startsWith("_newchannel: "))
        {
            Pattern P = Pattern.compile("\"([^\"]*)\"");
            Matcher m = P.matcher(message);
            if(m.find()) {
                chat.newChannel(user, m.group(1));
            }
        }
        else if(message.startsWith("_leavechannel")) {
            chat.removeUser(user);
        }
        else if(chat.getChannel(user)!="" && chat.getChannel(user).equals("chatbot")) {
            chat.channelBot(user, message);
        }
        else {
            chat.broadcastMessage(user, chat.getUsernameMap(user), message);
        }
    }

}
