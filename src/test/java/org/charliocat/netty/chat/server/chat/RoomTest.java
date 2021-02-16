package org.charliocat.netty.chat.server.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RoomTest {

    @Test
    void addEntriesToRoomHistoryAndRemoveWhenExceededHistorySize() {
        Room room = new Room(5, 5);
        for (int i = 0; i < 5; i++) {
            room.addToHistory("username", "message " + i);
        }

        assertThat(room.getHistory().get(0).getMessage()).isEqualTo("message 0");
        assertThat(room.getHistory().get(4).getMessage()).isEqualTo("message 4");

        room.addToHistory("username", "last message");

        assertThat(room.getHistory().get(0).getMessage()).isEqualTo("message 1");
        assertThat(room.getHistory().get(4).getMessage()).isEqualTo("last message");
    }

    @Test
    void addUserToFullRoomThrowsAndException() {
        Room room = new Room(5, 5);
        for (int i = 0; i < 5; i++) {
            room.addUser("username" + i);
        }

        Exception e = assertThrows(RuntimeException.class, () -> {
            room.addUser("newUser");
        });

        assertThat(e.getMessage()).isEqualTo(Room.MAX_USERS_ERROR);
    }

}