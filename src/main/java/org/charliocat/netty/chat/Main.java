package org.charliocat.netty.chat;

import static java.lang.System.exit;

import java.util.NoSuchElementException;
import java.util.Scanner;

import org.charliocat.netty.chat.server.ChatServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        int port = 8001;
        ChatServer server = new ChatServer(port);

        logger.info("Starting Server on port " + port);
        logger.info("Type 'terminate' to close the server");
        server.run();

        waitUntilTerminate();

        logger.info("Closing Server");
        server.terminate();

        exit(0);
    }

    private static void waitUntilTerminate() {
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                long then = System.currentTimeMillis();
                String line = scanner.nextLine();
                if (line.equals("terminate")) {
                    long now = System.currentTimeMillis();
                    logger.info(String.format("Server closed after running %.3fs%n", (now - then) / 1000d));
                    break;
                }
            }
        } catch (IllegalStateException | NoSuchElementException e) {
            // System.in has been closed
            logger.error("System.in was closed; exiting");
        }

    }

}
