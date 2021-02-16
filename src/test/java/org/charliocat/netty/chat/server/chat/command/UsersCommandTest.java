package org.charliocat.netty.chat.server.chat.command;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
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
class UsersCommandTest {

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
    void listUsersFromAChatRoom() {
        Collection<String> users = new ArrayList<>();
        users.add(USERNAME);
        users.add(USERNAME_2);
        when(broker.getSubscribers(TOPIC)).thenReturn(users);

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Session session = new Session(ctx, USERNAME);
        session.setCurrentTopic(TOPIC);

        chatCommand.handlers().get(ChatCommand.USERS_COMMAND)
                   .accept(session, new String[]{});

        verify(ctx).writeAndFlush(String.format("Users on topic %s", session.getCurrentTopic()));
        verify(ctx).writeAndFlush(String.format(" -%s", USERNAME));
    }

}