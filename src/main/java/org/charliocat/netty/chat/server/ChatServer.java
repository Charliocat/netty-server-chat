package org.charliocat.netty.chat.server;

import java.net.InetSocketAddress;

import org.charliocat.netty.chat.command.CommandExecutor;
import org.charliocat.netty.chat.command.Commander;
import org.charliocat.netty.chat.server.chat.InMemoryChat;
import org.charliocat.netty.chat.server.chat.command.ChatCommand;
import org.charliocat.netty.chat.server.chat.executor.Executor;
import org.charliocat.netty.chat.server.chat.executor.Scheduler;
import org.charliocat.netty.chat.server.encoder.CommandDecoder;
import org.charliocat.netty.chat.server.handler.AuthHandler;
import org.charliocat.netty.chat.session.InMemorySessionRepository;
import org.charliocat.netty.chat.session.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.codec.string.LineSeparator;
import io.netty.util.CharsetUtil;

public class ChatServer {

    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);
    private static final String EXECUTOR_WORKER_NAME = "serverExecutor";
    private static final Integer EXECUTOR_WORKER_THREADS = 4;
    private static final Integer BOSS_THREADS = 1;
    private static final Integer WORKER_THREADS = 4;
    public static final int USERNAMES_MAX_RANDOM_RANGE = 10_000;

    private final int port;
    private SessionRepository sessionRepository;
    private EventLoopGroup workerGroup;
    private EventLoopGroup bossGroup;
    private ChannelFuture channelFuture;
    private Router router;

    public ChatServer(int port) {
        this.port = port;

        InMemoryChat broker = new InMemoryChat(InMemoryChat.MAX_USERS_BY_ROOM, InMemoryChat.HISTORY_SIZE);
        sessionRepository = new InMemorySessionRepository();
        ChatCommand cmds = new ChatCommand(broker, sessionRepository);
        Commander commandExecutor = new CommandExecutor();
        commandExecutor.register(cmds);

        Executor executor = new Scheduler(EXECUTOR_WORKER_NAME, EXECUTOR_WORKER_THREADS);
        router = new Router(broker, commandExecutor, executor, sessionRepository);
    }

    public void run() throws Exception {
        bossGroup = new NioEventLoopGroup(BOSS_THREADS);
        workerGroup = new NioEventLoopGroup(WORKER_THREADS);

        try {
            final ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .localAddress(new InetSocketAddress("localhost", port))
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new ServerInitializer());

            channelFuture = bootstrap.bind(port).sync();
        } catch (Exception e) {
            logger.error("Error in server", e);
        }
    }

    public void terminate() {
        try {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("Terminate exception ", e.getMessage());
        }
    }

    private class ServerInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("frameDecoder",
                             new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()));
            pipeline.addLast("stringDecoder", new CommandDecoder(CharsetUtil.UTF_8));
            pipeline.addLast("lineEncoder", new LineEncoder(LineSeparator.UNIX, CharsetUtil.UTF_8));
            pipeline.addLast("handler", new AuthHandler(sessionRepository, router));
        }
    }

}
