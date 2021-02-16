package org.charliocat.netty.chat.server.chat.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
class ListCommandTest {

    private static final String USERNAME = "Kakarot";
    private static final String TOPIC = "Kamehouse";
    private static final String TOPIC_2 = "Namek";

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
    void listChatRooms() {
        List<String> topics = new ArrayList<>();
        topics.add(TOPIC);
        topics.add(TOPIC_2);
        when(broker.getTopics()).thenReturn(topics);

        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Session session = new Session(ctx, USERNAME);

        chatCommand.handlers().get(ChatCommand.LIST_COMMAND)
                   .accept(session, new String[] {});

        verify(ctx, times(1)).writeAndFlush(TOPIC);
        verify(ctx, times(1)).writeAndFlush(TOPIC_2);
    }

}
