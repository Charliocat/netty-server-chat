package org.charliocat.netty.chat.server.chat.command;

import java.util.function.BiConsumer;

import org.charliocat.netty.chat.server.chat.Chat;
import org.charliocat.netty.chat.session.Session;
import org.charliocat.netty.chat.session.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCommand {

    protected static final Logger logger = LoggerFactory.getLogger(Chat.class);

    protected Chat broker;
    protected SessionRepository repository;

    public AbstractCommand(Chat broker, SessionRepository repository) {
        this.broker = broker;
        this.repository = repository;
    }

    protected abstract BiConsumer<Session, String[]> execution();

    protected void send(String username, String msg) {
        Session session = repository.get(username);
        if (session == null) {
            logger.error("Session not found");
            return;
        }

        session.send(msg);
    }

    protected abstract String cmdUsage();

}
