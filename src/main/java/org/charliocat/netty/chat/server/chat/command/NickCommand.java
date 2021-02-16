package org.charliocat.netty.chat.server.chat.command;

import static org.charliocat.netty.chat.server.chat.command.ChatCommand.COMMAND_NOT_POSSIBLE_IN_ROOM;
import static org.charliocat.netty.chat.server.chat.command.ChatCommand.NICKNAME_TAKEN;

import java.util.function.BiConsumer;

import org.charliocat.netty.chat.server.chat.Chat;
import org.charliocat.netty.chat.session.Session;
import org.charliocat.netty.chat.session.SessionRepository;

public class NickCommand extends AbstractCommand {

    public NickCommand(Chat broker, SessionRepository repository) {
        super(broker, repository);
    }

    @Override
    protected BiConsumer<Session, String[]> execution() {
        return (session, arguments) -> {
            if (arguments.length != 1) {
                session.send(cmdUsage());
                return;
            }

            if (session.hasTopic()) {
                logger.info(COMMAND_NOT_POSSIBLE_IN_ROOM);
                session.send(COMMAND_NOT_POSSIBLE_IN_ROOM);
                return;
            }

            String nickname = arguments[0];
            logger.info("Nick ", nickname);

            if (repository.contains(nickname) || repository.containsNickname(nickname)) {
                logger.info(NICKNAME_TAKEN);
                session.send(NICKNAME_TAKEN);
                return;
            }

            repository.get(session.getUsername()).setNickname(nickname);
            repository.updateNickname(session.getNickname(), session.getUsername());
            session.send(String.format("Welcome %s", nickname));
        };
    }

    @Override
    protected String cmdUsage() {
        return "usage: /nick <nickname>";
    }

}
