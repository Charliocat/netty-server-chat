package org.charliocat.netty.chat.server.handler;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.charliocat.netty.chat.server.Router;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.netty.channel.embedded.EmbeddedChannel;

@ExtendWith(MockitoExtension.class)
class ClientHandlerTest {

    private static final String USERNAME = "Batman";

    @Mock
    private Router router;

    @Test
    public void onClientHandlerAddedRouterAcceptsSession() {
        EmbeddedChannel channel = new EmbeddedChannel(new ClientHandler(USERNAME, router));

        verify(router, times(1)).accept(any());

        String res = channel.readOutbound();

        assertTrue(res.equals("Welcome " + USERNAME));
    }

    @Test
    public void onConnectionClosedClientHandlerDetectsAndRouterCloseSession() {
        EmbeddedChannel channel = new EmbeddedChannel(new ClientHandler(USERNAME, router));

        channel.close();

        verify(router, times(1)).close(USERNAME);
    }

}