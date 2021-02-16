package org.charliocat.netty.chat.server;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.charliocat.netty.chat.command.CommandRequest;
import org.charliocat.netty.chat.command.Commander;
import org.charliocat.netty.chat.server.chat.Chat;
import org.charliocat.netty.chat.server.chat.executor.Executor;
import org.charliocat.netty.chat.session.Session;
import org.charliocat.netty.chat.session.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.netty.channel.ChannelHandlerContext;

@ExtendWith(MockitoExtension.class)
public class RouterTest {

    private static final String USERNAME = "foo";
    private static final String TOPIC = "topic";

    @Mock
    private Chat chat;

    @Mock
    private Commander commander;

    @Mock
    private Executor executor;

    @Mock
    private SessionRepository sessions;

    private Router router;

    @BeforeEach
    public void setUp() throws Exception {
        router = new Router(chat, commander, executor, sessions);
    }

    @Test
    public void routerAcceptSessionAndIsRegistered() {
        Session session = mock(Session.class);
        when(session.getUsername()).thenReturn(USERNAME);

        router.accept(session);

        verify(sessions, times(1)).add(session);
    }

    @Test
    public void routerCloseAndRemoveSession() {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Session session = new Session(ctx, USERNAME);
        session.setCurrentTopic(TOPIC);
        when(sessions.get(USERNAME)).thenReturn(session);

        router.close(USERNAME);

        verify(sessions, times(1)).remove(USERNAME);
        verify(chat, times(1)).unsubscribe(TOPIC, USERNAME);
    }

    @Test
    public void routerCannotCloseSessionBecauseNotFound() {
        when(sessions.get(USERNAME)).thenReturn(null);

        router.close(USERNAME);

        verify(sessions, never()).remove(USERNAME);
        verify(chat, never()).unsubscribe(TOPIC, USERNAME);
    }

    @Test
    public void routerCannotReceiveMessageBecauseSessionNotFound() {
        when(sessions.get(USERNAME)).thenReturn(null);
        CommandRequest cmdRequest = mock(CommandRequest.class);

        router.receiveMessage(USERNAME, cmdRequest);

        verify(executor, never()).execute(any());
    }

    @Test
    public void routerCannotReceiveMessageBecauseCommandNotFound() {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Session session = new Session(ctx, USERNAME);
        when(sessions.get(USERNAME)).thenReturn(session);

        CommandRequest cmdRequest = new CommandRequest("foo", new String[] { "arg1" });
        when(commander.contains(cmdRequest.getCmd())).thenReturn(false);

        router.receiveMessage(USERNAME, cmdRequest);

        verify(executor, never()).execute(any());
    }

    public void routerReceiveMessageAndExecutesCommand() {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Session session = new Session(ctx, USERNAME);
        when(sessions.get(USERNAME)).thenReturn(session);

        CommandRequest cmdRequest = new CommandRequest("foo", new String[] { "arg1" });
        when(commander.contains(cmdRequest.getCmd())).thenReturn(true);

        router.receiveMessage(USERNAME, cmdRequest);

        verify(executor, times(1)).execute(any());
        verify(commander, times(1)).execute(session, cmdRequest);
    }

}