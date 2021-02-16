package org.charliocat.netty.chat.server.chat;

import java.time.LocalTime;

public class Entry {

    private String message;
    private String username;
    private LocalTime time;

    public Entry(String username, String message) {
        this.username = username;
        this.message = message;
        this.time = LocalTime.now();
    }

    public LocalTime getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

}
