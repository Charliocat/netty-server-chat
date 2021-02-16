package org.charliocat.netty.chat.command;

import org.charliocat.netty.chat.session.Session;

public interface Commander {

    void register(CommandHandler commandHandler);

    void execute(Session session, CommandRequest cmd);

    boolean contains(String cmdType);

}
