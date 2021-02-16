package org.charliocat.netty.chat.server.chat.command;

import static org.charliocat.netty.chat.server.chat.command.ChatCommand.COMMAND_NOT_POSSIBLE_IN_ROOM;

import java.util.function.BiConsumer;

import org.charliocat.netty.chat.server.chat.Chat;
import org.charliocat.netty.chat.session.Session;
import org.charliocat.netty.chat.session.SessionRepository;

public class ListCommand extends AbstractCommand {

    public ListCommand(Chat broker, SessionRepository repository) {
        super(broker, repository);
    }

    @Override
    protected BiConsumer<Session, String[]> execution() {
        return (session, arguments) -> {
            logger.info("list");

            if (arguments.length != 0) {
                session.send(cmdUsage());
                throw new RuntimeException("Unexpected command arguments size");
            }

            if (session.hasTopic()) {
                logger.info(COMMAND_NOT_POSSIBLE_IN_ROOM);
                session.send(COMMAND_NOT_POSSIBLE_IN_ROOM);
                return;
            }

            for (String topic : broker.getTopics()) {
                session.send(topic);
            }
        };
    }

    @Override
    protected String cmdUsage() {
        return "usage: /list";
    }

}
