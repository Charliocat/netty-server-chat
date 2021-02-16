package org.charliocat.netty.chat.server.chat.command;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.charliocat.netty.chat.command.CommandHandler;
import org.charliocat.netty.chat.server.chat.Chat;
import org.charliocat.netty.chat.session.Session;
import org.charliocat.netty.chat.session.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.netty.channel.ChannelHandlerContext;

@ExtendWith(MockitoExtension.class)
class ExitCommandTest {

    private static final String USERNAME = "Kakarot";
    private static final String TOPIC = "Kamehouse";
    private static final String USERNAME_2 = "Raditz";

    @Mock
    private Chat broker;

    @Mock
    private SessionRepository repository;

    private CommandHandler chatCommand;

    @BeforeEach
    void setUp() {
        chatCommand = new ChatCommand(broker, repository);
    }

    @Test
    void userLeavesRoomAndAllUsersAreNotified() {
        Collection<String> users = new ArrayList<>();
        users.add(USERNAME_2);
        when(broker.getSubscribers(TOPIC)).thenReturn(users);

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Session session = new Session(ctx, USERNAME);
        session.setCurrentTopic(TOPIC);

        ChannelHandlerContext ctxB = mock(ChannelHandlerContext.class);
        Session sessionB = new Session(ctxB, USERNAME_2);
        sessionB.setCurrentTopic(TOPIC);
        when(repository.get(USERNAME_2)).thenReturn(sessionB);

        chatCommand.handlers().get(ChatCommand.EXIT_COMMAND)
                   .accept(session, new String[]{});

        verify(broker, times(1)).unsubscribe(TOPIC, USERNAME);
        verify(ctx, times(1)).writeAndFlush("Bye Bye!");
        verify(ctx, times(1)).close();
        assertFalse(session.hasTopic());
        verify(repository).removeNickname(USERNAME);

        verify(ctxB, times(1)).writeAndFlush(USERNAME + " has left!");
    }

}
