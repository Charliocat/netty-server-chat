package org.charliocat.netty.chat.session;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.ChannelHandlerContext;

public class InMemorySessionRepository implements SessionRepository {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Set<String> nicknames = ConcurrentHashMap.newKeySet();

    @Override
    public void add(Session session) {
        sessions.put(session.getUsername(), session);
        nicknames.add(session.getNickname());
    }

    @Override
    public Session get(String username) {
        return sessions.get(username);
    }

    @Override
    public void remove(String username) {
        sessions.remove(username);
    }

    @Override
    public Boolean contains(String username) {
        return sessions.containsKey(username);
    }

    @Override
    public void updateNickname(String nickname, String username) {
        nicknames.remove(username);
        nicknames.remove(nickname);
        nicknames.add(nickname);
    }

    @Override
    public void removeNickname(String nickname) {
        nicknames.remove(nickname);
    }

    @Override
    public Boolean containsNickname(String nickname) {
        return nicknames.contains(nickname);
    }

    public static Session buildSession(ChannelHandlerContext ctx, String username) {
        return new Session(ctx, username);
    }

}
