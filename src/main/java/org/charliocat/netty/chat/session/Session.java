package org.charliocat.netty.chat.session;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.Queue;

import io.netty.channel.ChannelHandlerContext;

public class Session {

    private static final int DEFAULT_MESSAGE_PER_MINUTE_LIMIT = 30;
    private final ChannelHandlerContext ctx;
    private volatile String username;
    private volatile String nickname;
    private volatile String currentTopic;
    private final int messageLimit;
    private volatile Queue<LocalTime> messageLimitWindow;

    public Session(final ChannelHandlerContext ctx,
                   final String username,
                   final int messageLimit) {
        this.ctx = ctx;
        this.username = username;
        this.nickname = username;
        this.messageLimit = messageLimit;
        this.messageLimitWindow = new LinkedList<>();
    }

    public Session(final ChannelHandlerContext ctx, final String username) {
        this(ctx, username, DEFAULT_MESSAGE_PER_MINUTE_LIMIT);
    }

    public String getUsername() {
        return username;
    }

    public String getCurrentTopic() {
        return currentTopic;
    }

    public void setCurrentTopic(String currentTopic) {
        this.currentTopic = currentTopic;
    }

    public void clearTopic() {
        currentTopic = null;
    }

    public Boolean hasTopic() {
        return currentTopic != null;
    }

    public synchronized void send(String msg) {
        ctx.writeAndFlush(msg);
    }

    public void terminate() {
        ctx.close();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public synchronized Boolean messageLimitReached() {
        messageLimitWindow.add(LocalTime.now());
        if (messageLimitWindow.size() < messageLimit) {
            return false;
        }

        //Clean out old Timestamps
        while (LocalTime.now().isAfter(messageLimitWindow.peek().plusMinutes(1))) {
            messageLimitWindow.remove();
        }

        return messageLimitWindow.size() > messageLimit - 1;
    }

}
