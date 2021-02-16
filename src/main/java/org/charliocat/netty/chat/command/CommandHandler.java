package org.charliocat.netty.chat.command;

import java.util.Map;
import java.util.function.BiConsumer;

import org.charliocat.netty.chat.session.Session;

public interface CommandHandler {

    // Command Handler definitions
    Map<String, BiConsumer<Session, String[]>> handlers() throws RuntimeException;

}

