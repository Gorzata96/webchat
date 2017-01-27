import org.apache.log4j.BasicConfigurator;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;
import static spark.Spark.*;

/**
 * Created by Gosia on 2017-01-16.
 */
public class Chat {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        Chat chat = new Chat();
    }

    private Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    private Map<Session, String> userChannelMap = new ConcurrentHashMap<>();
    private Set<String> channels = new HashSet<>();

    public Chat(){
        staticFileLocation("/public");
        ChatWebSocketHandler webSocketHandler = new ChatWebSocketHandler(this);
        webSocket("/chat", webSocketHandler);
        init();
        channels.add("chatbot");
    }

    public void change(Session user, String channel) {
        userChannelMap.put(user, channel);
        channels.add(channel);
    }

    public void remove(Session user) {
        String username = userUsernameMap.get(user);
        userUsernameMap.remove(user);
        broadcastMessage(user, "Server", username + " opuścił chat.");
        userChannelMap.remove(user);
    }


    public void broadcastMessage(Session user, String author, String msg) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach( session -> {
            try {
                String channel = userChannelMap.get(user);
                if(userChannelMap.get(session).equals(channel)) {
                    List<Session> usersOnChannel = userChannelMap.entrySet().stream()
                            .filter(e -> e.getValue().equals(channel))
                            .map(Map.Entry::getKey).collect(Collectors.toList());
                    List<String> usernamesOnChannel = userUsernameMap.entrySet().stream()
                            .filter(e -> usersOnChannel.contains(e.getKey()))
                            .map(Map.Entry::getValue).collect(Collectors.toList());
                    session.getRemote().sendString(String.valueOf(new JSONObject()
                            .put("userMessage", createHtmlMessageFromSender(author, msg))
                            .put("userlist", usernamesOnChannel)
                            .put("channelname", channel)
                            .put("channellist", channels)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " pisze:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }

    public void putOnUsernameMap(Session user, String username) {
        userUsernameMap.put(user, username);
    }

    public String getUsernameMap(Session user) {
        return userUsernameMap.get(user);
    }

    public String getChannel(Session user) {
        return userChannelMap.get(user);
    }

    public void removeUser(Session user) {
        broadcastMessage(user, "Server", getUsernameMap(user) + " opuścił channel.");
        userChannelMap.remove(user);
    }

    public void joinChat(Session user, String group) {
        putOnUsernameMap(user, group);
        change(user, "General");
        broadcastMessage(user, "Server", (group + " dołączył do chatu."));
    }

    public void newChannel(Session user, String group) {
        change(user, group);
        broadcastMessage(user, "Server", (getUsernameMap(user) + " zmienił channel na: " + group));
    }

    public void channelBot(Session user, String message) {
        Chatbot chatbot = new Chatbot();
        broadcastMessage(user, getChannel(user), message);
        broadcastMessage(user, "Chatbot", chatbot.answer(message));
    }
}
