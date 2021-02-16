package org.charliocat.netty.chat.server.encoder;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.CharBuffer;

import org.charliocat.netty.chat.command.CommandRequest;
import org.charliocat.netty.chat.server.chat.command.ChatCommand;
import org.junit.jupiter.api.Test;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

class CommandDecoderTest {

    @Test
    public void decodeRawMessageToCommand(){
        EmbeddedChannel channel = new EmbeddedChannel(
                new StringEncoder(), new CommandDecoder(CharsetUtil.UTF_8) );

        channel.writeInbound(ByteBufUtil.encodeString(new PooledByteBufAllocator(), CharBuffer
                .wrap("/nick Spiderman" ), CharsetUtil.UTF_8));

        CommandRequest cmd = channel.readInbound();

        assertNotNull(cmd);
        assertTrue(cmd.getCmd().equals(ChatCommand.NICKNAME_COMMAND));
        assertEquals(1, cmd.getArguments().length);
        assertTrue(cmd.getArguments()[0].equals("Spiderman"));
    }

    @Test
    public void decodeRawMessageToPublishCommand(){
        EmbeddedChannel channel = new EmbeddedChannel(
                new StringEncoder(), new CommandDecoder(CharsetUtil.UTF_8) );

        channel.writeInbound(ByteBufUtil.encodeString(new PooledByteBufAllocator(), CharBuffer
                .wrap("hello there" ), CharsetUtil.UTF_8));

        CommandRequest cmd = channel.readInbound();

        assertNotNull(cmd);
        assertTrue(cmd.getCmd().equals(ChatCommand.PUBLISH_COMMAND));
        assertEquals(1, cmd.getArguments().length);
        assertTrue(cmd.getArguments()[0].equals("hello there"));
    }

}