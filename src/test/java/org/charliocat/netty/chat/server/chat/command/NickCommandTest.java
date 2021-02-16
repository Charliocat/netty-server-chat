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
class NickCommandTest {

    private static final String USERNAME = "Kakarot";

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
    void userChangesNickname() {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Session session = new Session(ctx, USERNAME);

        when(repository.get(USERNAME)).thenReturn(session);

        chatCommand.handlers().get(ChatCommand.NICKNAME_COMMAND)
                   .accept(session, new String[] { "nickname" });

        verify(repository).updateNickname("nickname", USERNAME);
        verify(ctx, times(1)).writeAndFlush("Welcome nickname");
    }

    @Test
    void userChangesNicknameButItIsTaken() {
        when(repository.contains("nickname")).thenReturn(true);

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Session session = new Session(ctx, USERNAME);

        chatCommand.handlers().get(ChatCommand.NICKNAME_COMMAND)
                   .accept(session, new String[] { "nickname" });

       verify(ctx, times(1)).writeAndFlush("Nickname already exists!");
    }

}
