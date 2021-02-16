package org.charliocat.netty.chat.server.chat.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.charliocat.netty.chat.command.CommandHandler;
import org.charliocat.netty.chat.server.Router;
import org.charliocat.netty.chat.server.chat.Chat;
import org.charliocat.netty.chat.session.Session;
import org.charliocat.netty.chat.session.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatCommand implements CommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(Router.class);

    public static final String NO_TOPIC_ASSIGNED = "No session topic!";
    public static final String COMMAND_NOT_POSSIBLE_IN_ROOM = "Can't execute this command in a room!";
    public static final String NICKNAME_TAKEN = "Nickname already exists!";
    public static final String JOIN_COMMAND = "join";
    public static final String EXIT_COMMAND = "exit";
    public static final String USERS_COMMAND = "users";
    public static final String PUBLISH_COMMAND = "publish";
    public static final String NICKNAME_COMMAND = "nick";
    public static final String LIST_COMMAND = "list";

    private final Chat broker;
    private final SessionRepository repository;

    public ChatCommand(Chat broker, SessionRepository repository) {
        this.broker = broker;
        this.repository = repository;
    }

    public Map<String, BiConsumer<Session, String[]>> handlers() throws RuntimeException {
        Map<String, BiConsumer<Session, String[]>> handlers = new HashMap<>();
        handlers.put(JOIN_COMMAND, new JoinCommand(broker, repository).execution());
        handlers.put(EXIT_COMMAND, new ExitCommand(broker, repository).execution());
        handlers.put(USERS_COMMAND, new UsersCommand(broker, repository).execution());
        handlers.put(NICKNAME_COMMAND, new NickCommand(broker, repository).execution());
        handlers.put(LIST_COMMAND, new ListCommand(broker, repository).execution());
        handlers.put(PUBLISH_COMMAND, this::publish);

        return handlers;
    }

    private void publish(Session session, String[] arguments) {
        if (arguments.length != 1) {
            throw new RuntimeException("Unexpected command arguments size");
        }

        if (!session.hasTopic()) {
            session.send(NO_TOPIC_ASSIGNED);
            return;
        }

        String message = arguments[0];
        if (session.messageLimitReached()) {
            session.send("Message limit reached. Wait a while");
            return;
        }
        for (String username : broker.getSubscribers(session.getCurrentTopic())) {
            if (username.equals(session.getUsername())) { continue; }

            send(username, session.getNickname() + ": " + message);
        }
        broker.addToHistory(session.getCurrentTopic(), session.getNickname(), message);
    }

    private void send(String username, String msg) {
        Session session = repository.get(username);
        if (session == null) {
            logger.error("Session not found");
            return;
        }

        session.send(msg);
    }

}
