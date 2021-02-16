package org.charliocat.netty.chat.server.chat;

import java.util.Collection;
import java.util.List;

public interface Chat {

    void subscribe(String topic, String username);

    void unsubscribe(String topic, String username);

    Collection<String> getSubscribers(String topic);

    void addToHistory(String topic, String username, String msg);

    List<Entry> getHistory(String topic);

    List<String> getTopics();

}
