package org.charliocat.netty.chat.server.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.charliocat.netty.chat.server.Router;
import org.charliocat.netty.chat.session.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.netty.channel.embedded.EmbeddedChannel;

@ExtendWith(MockitoExtension.class)
class AuthHandlerTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private Router router;

    @Captor
    private ArgumentCaptor<String> usernameCaptor;

    private AuthHandler authHandler;

    @BeforeEach
    void setUp() {
        authHandler = new AuthHandler(sessionRepository, router);
    }

    @Test
    void generatesNewUsernameWhenNewClientIsConnected() {
        EmbeddedChannel channel = new EmbeddedChannel(authHandler);

        verify(sessionRepository).contains(usernameCaptor.capture());
        verify(router, times(1)).accept(any());

        String res = channel.readOutbound();
        assertThat(res).isEqualTo("Welcome " + usernameCaptor.getValue());
    }

}