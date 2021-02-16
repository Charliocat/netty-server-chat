package org.charliocat.netty.chat.server.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class InMemoryChatTest {

    private static final String USERNAME = "username";
    private static final String TOPIC = "topic";

    private final Chat chat = new InMemoryChat(InMemoryChat.MAX_USERS_BY_ROOM,
                                               InMemoryChat.HISTORY_SIZE);

    @Test
    void subscribeUserToTopic() {
        chat.subscribe(TOPIC, USERNAME);

        assertThat(chat.getSubscribers(TOPIC).size()).isEqualTo(1);
    }

    @Test
    void subscribeUserToAlreadySubscribedTopic() {
        chat.subscribe(TOPIC, USERNAME);

        Exception e = assertThrows(RuntimeException.class, () -> {
            chat.subscribe(TOPIC, USERNAME);
        } );

        assertThat(e.getMessage()).isEqualTo(Room.USER_ALREADY_JOINED);
    }

    @Test
    void unsubscribeUserFromExistingTopic() {
        chat.subscribe(TOPIC, USERNAME);
        chat.subscribe(TOPIC, "Batman");

        assertThat(chat.getSubscribers(TOPIC).size()).isEqualTo(2);

        chat.unsubscribe(TOPIC, USERNAME);
        assertThat(chat.getSubscribers(TOPIC).size()).isEqualTo(1);
    }

    @Test
    void unsubscribeUserFromExistingTopicAndRemoveTopicBecauseNoMoreUsers() {
        chat.subscribe(TOPIC, USERNAME);

        chat.unsubscribe(TOPIC, USERNAME);
        assertTrue(chat.getSubscribers(TOPIC).isEmpty());
        assertTrue(chat.getTopics().isEmpty());
    }

    @Test
    void addMessageToHistory() {
        chat.addToHistory(TOPIC, USERNAME, "Hello");

        List<Entry> history = chat.getHistory(TOPIC);
        assertThat(history.size()).isEqualTo(1);
        assertThat(history.get(0).getMessage()).isEqualTo("Hello");
        assertThat(history.get(0).getUsername()).isEqualTo(USERNAME);
    }

}