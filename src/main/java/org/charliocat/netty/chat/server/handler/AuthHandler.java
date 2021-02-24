package org.charliocat.netty.chat.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.charliocat.netty.chat.command.CommandRequest;
import org.charliocat.netty.chat.server.ChatServer;
import org.charliocat.netty.chat.server.Router;
import org.charliocat.netty.chat.session.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class AuthHandler extends SimpleChannelInboundHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthHandler.class);

    private SessionRepository sessionRepository;
    private Router router;

    public AuthHandler(SessionRepository sessionRepository, Router router) {
        this.sessionRepository = sessionRepository;
        this.router = router;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        String username = generateRandomUsername();

        while (sessionRepository.contains(username)) {
            username = generateRandomUsername();
        }

        ctx.pipeline().addLast(new ClientHandler(username, router));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        CommandRequest request = (CommandRequest) msg;
        logger.info(request.getCmd());
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception caught ", cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    private String generateRandomUsername() {
        int number = new Random().nextInt(ChatServer.USERNAMES_MAX_RANDOM_RANGE) + 1;
        return "user" + number;
    }

}
