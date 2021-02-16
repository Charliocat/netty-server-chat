package org.charliocat.netty.chat.server.handler;

import java.util.Random;

import org.charliocat.netty.chat.command.CommandRequest;
import org.charliocat.netty.chat.server.ChatServer;
import org.charliocat.netty.chat.server.Router;
import org.charliocat.netty.chat.session.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuthHandler extends SimpleChannelInboundHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthHandler.class);

    private SessionRepository sessionRepository;
    private Router router;
    private Boolean authorized = false;

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

        authorized = true;
        ctx.pipeline().addLast(new ClientHandler(username, router));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        CommandRequest request = (CommandRequest) msg;
        logger.info(request.getCmd());

        if (authorized) {
            ctx.fireChannelRead(msg);
            return;
        }
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
