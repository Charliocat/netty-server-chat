package org.charliocat.netty.chat.server.chat.command;

import static org.charliocat.netty.chat.server.chat.command.ChatCommand.NO_TOPIC_ASSIGNED;

import java.util.function.BiConsumer;

import org.charliocat.netty.chat.server.chat.Chat;
import org.charliocat.netty.chat.session.Session;
import org.charliocat.netty.chat.session.SessionRepository;

public class UsersCommand extends AbstractCommand {

    public UsersCommand(Chat broker, SessionRepository repository) {
        super(broker, repository);
    }

    @Override
    protected BiConsumer<Session, String[]> execution() {
        return (session, arguments) -> {
            logger.info("Users");

            if (!session.hasTopic()) {
                session.send(NO_TOPIC_ASSIGNED);
                return;
            }

            session.send(String.format("Users on topic %s", session.getCurrentTopic()));
            for (String username : broker.getSubscribers(session.getCurrentTopic())) {
                session.send(String.format(" -%s", username));
            }
        };
    }

    @Override
    protected String cmdUsage() {
        return "usage: /users";
    }

}
