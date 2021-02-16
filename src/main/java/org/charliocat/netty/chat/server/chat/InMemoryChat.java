package org.charliocat.netty.chat.server.chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryChat implements Chat {

    public static final Integer MAX_USERS_BY_ROOM = 10;
    public static final Integer HISTORY_SIZE = 5;

    private volatile Map<String, Room> subscribers = new ConcurrentHashMap<>();
    private final Integer maxUsersByRoom;
    private final Integer historySize;

    public InMemoryChat(Integer maxUsersByRoom, Integer historySize) {
        this.maxUsersByRoom = maxUsersByRoom;
        this.historySize = historySize;
    }

    public synchronized void subscribe(String topic, String username) {
        if (!subscribers.containsKey(topic)) {
            subscribers.put(topic, new Room(maxUsersByRoom, historySize));
        }

        Room room = subscribers.get(topic);
        if (room.hasUser(username)) {
            throw new RuntimeException(Room.USER_ALREADY_JOINED);
        }

        room.addUser(username);
    }

    public synchronized void unsubscribe(String topic, String username) {
        Room room = subscribers.get(topic);
        if (room == null) {
            return;
        }

        room.removeUser(username);

        if (room.subscribers().isEmpty()) {
            subscribers.remove(topic, room);
        }
    }

    public synchronized Collection<String> getSubscribers(String topic) {
        if (!subscribers.containsKey(topic)) {
            return new ArrayList<>();
        }

        return subscribers.get(topic).subscribers();
    }

    public void addToHistory(String topic, String username, String msg) {
        Room room = subscribers.get(topic);
        if (room == null) {
            room = new Room(maxUsersByRoom, historySize);
            subscribers.put(topic, room);
        }

        room.addToHistory(username, msg);
    }

    public List<Entry> getHistory(String topic) {
        Room room = subscribers.get(topic);
        if (room == null) {
            return new ArrayList<>();
        }

        return room.getHistory();
    }

    @Override
    public List<String> getTopics() {
        return new ArrayList<>(subscribers.keySet());
    }

}
