package org.charliocat.netty.chat.server;

import org.charliocat.netty.chat.command.CommandRequest;
import org.charliocat.netty.chat.command.Commander;
import org.charliocat.netty.chat.server.chat.Chat;
import org.charliocat.netty.chat.server.chat.executor.Executor;
import org.charliocat.netty.chat.session.Session;
import org.charliocat.netty.chat.session.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Router {

    private static final Logger logger = LoggerFactory.getLogger(Router.class);

    public static String COMMAND_NOT_FOUND = "Command not found!";
    public static String UNEXPECTED_ERROR = "Execution Error";

    private Chat broker;
    private SessionRepository sessions;
    private Executor executor;
    private Commander commandExecutor;

    public Router(Chat broker, Commander cmdExec, Executor executor, SessionRepository sessions) {
        this.broker = broker;
        this.commandExecutor = cmdExec;
        this.executor = executor;
        this.sessions = sessions;
    }

    public void accept(Session session) {
        logger.info("Router accept ", session.getUsername());
        sessions.add(session);
    }

    public void close(String username) {
        Session session = sessions.get(username);
        if (session == null) {
            logger.error("Session not found on close ", username);
            return;
        }

        sessions.remove(username);
        if (session.hasTopic()) {
            broker.unsubscribe(session.getCurrentTopic(), username);
        }
    }

    public void receiveMessage(String username, CommandRequest cmd) {
        Session session = sessions.get(username);
        if (session == null) {
            logger.error("Session not found on userName %s", username);
            return;
        }

        if (!commandExecutor.contains(cmd.getCmd())) {
            session.send(COMMAND_NOT_FOUND);
            return;
        }

        // Delegate command execution to its own thread pool
        executor.execute(() -> {
            logger.info(String.format("User %s Received CMD: %s", username, cmd.getCmd()));
            try {
                commandExecutor.execute(session, cmd);
            } catch (Exception e) {
                logger.error("Unexpected exception executing task ", e.getMessage());
                session.send(UNEXPECTED_ERROR);
                e.printStackTrace();
            }
        });
    }
}
