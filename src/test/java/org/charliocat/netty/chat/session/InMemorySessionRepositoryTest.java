package org.charliocat.netty.chat.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import io.netty.channel.ChannelHandlerContext;

class InMemorySessionRepositoryTest {

    private static final String NICKNAME = "nickname";
    public static final String USERNAME = "username";
    private SessionRepository repository = new InMemorySessionRepository();

    @Test
    void addSession() {
        ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
        Session session = new Session(ctx, USERNAME);
        repository.add(session);

        assertTrue(repository.contains(USERNAME));
        assertTrue(repository.containsNickname(USERNAME));
        assertThat(repository.get(USERNAME)).isEqualTo(session);
    }

    @Test
    void updateNickname() {
        repository.updateNickname(NICKNAME, USERNAME);

        assertTrue(repository.containsNickname(NICKNAME));
        assertFalse(repository.containsNickname(USERNAME));
    }
}