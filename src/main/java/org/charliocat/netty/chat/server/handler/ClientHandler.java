package org.charliocat.netty.chat.server.handler;

import org.charliocat.netty.chat.command.CommandRequest;
import org.charliocat.netty.chat.server.Router;
import org.charliocat.netty.chat.session.InMemorySessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private String userName;
    private Router router;

    public ClientHandler(String userName, Router router) {
        this.userName = userName;
        this.router = router;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        try {
            router.accept(InMemorySessionRepository.buildSession(ctx, userName));
            ctx.writeAndFlush(String.format("Welcome %s", userName));
        } catch (Exception e) {
            logger.error("Unexpected exception", e.getMessage());
            ctx.writeAndFlush(e.getMessage());
            ctx.close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            CommandRequest req = (CommandRequest) msg;
            router.receiveMessage(userName, req);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        router.close(userName);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception caught ", cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

}
