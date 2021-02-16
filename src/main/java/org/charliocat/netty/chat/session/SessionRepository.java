package org.charliocat.netty.chat.session;

public interface SessionRepository {

    void add(Session session);

    Session get(String username);

    void remove(String username);

    Boolean contains(String username);

    void updateNickname(String nickname, String username);

    void removeNickname(String nickname);

    Boolean containsNickname(String nickname);
}
