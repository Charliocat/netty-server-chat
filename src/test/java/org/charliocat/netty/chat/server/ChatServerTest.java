package org.charliocat.netty.chat.server;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChatServerTest {

    private static final int PORT = 8001;

    private ChatServer server;

    @BeforeEach
    void setUp() throws Exception {
        server = new ChatServer(PORT);
        server.run();
    }

    @AfterEach
    void tearDown() {
        server.terminate();
    }

    @Test
    void singleClientChangeNicknameTest() throws Exception {
        Client clientA = new Client();

        clientA.readLine();
        clientA.write("/nick foo\n");
        String resA = clientA.readLine();
        assertTrue(resA.equals("Welcome foo"));

        clientA.close();
    }

    class Client {

        private BufferedWriter bufferedWriter;
        private BufferedReader bufferedReader;

        Client() throws IOException {
            InetAddress inetAddress = InetAddress.getByName("localhost");
            SocketAddress socketAddress = new InetSocketAddress(inetAddress, PORT);

            // create a socket
            Socket socket = new Socket();

            // this method will block no more than timeout ms.
            int timeoutInMs = 10 * 1000;   // 10 seconds
            socket.connect(socketAddress, timeoutInMs);

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        public void write(String msg) throws IOException {
            bufferedWriter.write(msg);
            bufferedWriter.flush();
        }

        public String readLine() throws IOException {
            return bufferedReader.readLine();
        }

        public void close() throws IOException {
            bufferedReader.close();
        }
    }

}