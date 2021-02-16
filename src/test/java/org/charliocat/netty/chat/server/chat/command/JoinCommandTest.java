package org.charliocat.netty.chat.server.chat.command;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.charliocat.netty.chat.command.CommandHandler;
import org.charliocat.netty.chat.server.chat.Chat;
import org.charliocat.netty.chat.server.chat.Room;
import org.charliocat.netty.chat.session.Session;
import org.charliocat.netty.chat.session.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.netty.channel.ChannelHandlerContext;

@ExtendWith(MockitoExtension.class)
class JoinCommandTest {

    private static final String USERNAME = "Kakarot";
    private static final String TOPIC = "Kamehouse";

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
    void userJoinsRoomTopic() {
        Collection<String> users = new ArrayList<>();
        users.add(USERNAME);
        when(broker.getSubscribers(TOPIC)).thenReturn(users);

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Session session = new Session(ctx, USERNAME);

        chatCommand.handlers().get(ChatCommand.JOIN_COMMAND)
                   .accept(session, new String[] { TOPIC });

        verify(ctx, times(1))
                .writeAndFlush(String.format("Welcome to room %s, there are %d users connected.", TOPIC,
                                             users.size()));
    }

    @Test
    void userCannotJoinFullRoom() {
        doThrow(new RuntimeException(Room.MAX_USERS_ERROR)).when(broker).subscribe(TOPIC, USERNAME);

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Session session = new Session(ctx, USERNAME);

        chatCommand.handlers().get(ChatCommand.JOIN_COMMAND)
                   .accept(session, new String[] { TOPIC });

        verify(ctx).writeAndFlush("Rejected: No free space on this room");
    }

}