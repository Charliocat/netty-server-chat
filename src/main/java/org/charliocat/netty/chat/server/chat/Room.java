package org.charliocat.netty.chat.server.chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Room {

    public static String MAX_USERS_ERROR = "No free space on this room";
    public static String USER_ALREADY_JOINED = "User already joined";

    private Integer maxUsers;
    private Integer historySize;
    private List<Entry> entries = Collections.synchronizedList(new ArrayList<>());
    private Collection<String> users = Collections.synchronizedList(new ArrayList<>());

    public Room(Integer maxUsers, Integer historySize) {
        this.maxUsers = maxUsers;
        this.historySize = historySize;
    }

    public synchronized void addToHistory(String username, String msg) {
        entries.add(new Entry(username, msg));

        if (entries.size() > historySize) {
            entries.remove(0);
        }
    }

    public synchronized List<Entry> getHistory() {
        return entries;
    }

    public synchronized void addUser(String username) {
        if (users.size() >= maxUsers) {
            throw new RuntimeException(MAX_USERS_ERROR);
        }

        users.add(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public Boolean hasUser(String username) {
        return users.contains(username);
    }

    public Collection<String> subscribers() {
        return users;
    }

}
