package org.charliocat.netty.chat.server.chat.command;

import java.util.function.BiConsumer;

import org.charliocat.netty.chat.server.chat.Chat;
import org.charliocat.netty.chat.server.chat.Entry;
import org.charliocat.netty.chat.session.Session;
import org.charliocat.netty.chat.session.SessionRepository;

public class JoinCommand extends AbstractCommand {

    public JoinCommand(Chat broker, SessionRepository repository) {
        super(broker, repository);
    }

    @Override
    protected BiConsumer<Session, String[]> execution() {
        return (session, arguments) -> {
            if (arguments.length != 1) {
                session.send(cmdUsage());
                return;
            }
            String topic = arguments[0];
            logger.info("Join ", topic);

            try {
                broker.subscribe(topic, session.getUsername());
            } catch (Exception e) {
                session.send(String.format("Rejected: %s", e.getMessage()));
                return;
            }

            session.setCurrentTopic(topic);
            int users = broker.getSubscribers(topic).size();
            session.send(String.format("Welcome to room %s, there are %d users connected.", topic, users));
            for (Entry msg : broker.getHistory(topic)) {
                session.send(String.format("%s: %s", msg.getUsername(), msg.getMessage()));
            }
        };
    }

    @Override
    protected String cmdUsage() {
        return "usage: /join <room_name>";
    }
}
