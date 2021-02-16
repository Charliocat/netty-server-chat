package org.charliocat.netty.chat.server.chat.command;

import java.util.function.BiConsumer;

import org.charliocat.netty.chat.server.chat.Chat;
import org.charliocat.netty.chat.session.Session;
import org.charliocat.netty.chat.session.SessionRepository;

public class ExitCommand extends AbstractCommand {

    public ExitCommand(Chat broker, SessionRepository repository) {
        super(broker, repository);
    }

    @Override
    protected BiConsumer<Session, String[]> execution() {
        return (session, arguments) -> {
            logger.info("Exit ");

            if (!session.hasTopic()) {
                return;
            }

            broker.unsubscribe(session.getCurrentTopic(), session.getUsername());
            for (String userName : broker.getSubscribers(session.getCurrentTopic())) {
                send(userName, session.getNickname() + " has left!");
            }

            session.clearTopic();
            session.send("Bye Bye!");
            session.terminate();

            repository.removeNickname(session.getNickname());
        };
    }

    @Override
    protected String cmdUsage() {
        return "usage /exit";
    }

}
