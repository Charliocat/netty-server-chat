package org.charliocat.netty.chat.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.charliocat.netty.chat.session.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.netty.channel.ChannelHandlerContext;

class CommandExecutorTest {

    private static final String CMD = "dummy";

    private ListAppender<ILoggingEvent> listAppender;
    private CommandExecutor commandExecutor;

    @BeforeEach
    void setUp() {
        prepareLogger();

        commandExecutor = new CommandExecutor();
        commandExecutor.register(new CommandHandler() {
            Map<String, BiConsumer<Session, String[]>> commands = new HashMap<>();

            @Override
            public Map<String, BiConsumer<Session, String[]>> handlers() throws RuntimeException {
                commands.put(CMD, (session, arguments) -> {
                    session.send(String.format("received %s", arguments[0]));
                });

                return commands;
            }
        });
    }

    @AfterEach
    void tearDown() {
        listAppender.clearAllFilters();
        listAppender.stop();
    }

    @Test
    void containsDummyCommand() {
        assertTrue(commandExecutor.contains(CMD));
    }

    @Test
    void executeDummyCommand() {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Session session = new Session(ctx, "username");

        commandExecutor.execute(session, new CommandRequest(CMD, new String[] { "hello" }));

        verify(ctx, times(1)).writeAndFlush("received hello");
    }

    @Test
    void logsErrorBecauseCommandNotFound() {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Session session = new Session(ctx, "username");

        commandExecutor.execute(session, new CommandRequest("foo", new String[] { "hello" }));

        verify(ctx, never()).writeAndFlush("received hello");
        assertThat(listAppender.list.size()).isEqualTo(1);
    }

    private void prepareLogger() {
        listAppender = new ListAppender<>();
        listAppender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(CommandExecutor.class);
        logger.addAppender(listAppender);
    }

}