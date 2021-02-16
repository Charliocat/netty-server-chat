# Netty-Chat-Server
Simple netty chat server &amp; client

## Requirements
- Java 11 : [JDK 11]
- Gradle 6.8 : [Gradle 6.8]

## Build the application

- Build with Gradle wrapper:
```
./gradlew clean build
```

- Build with Gradle version 6.8:
```
gradle clean build
```

## Run the application
- Run Server from eclipse running as java application in Main class:
```
org.charliocat.netty.chat.Main;
```

- Run Server jar with:
```
java -jar build/libs/netty-chat-1.0.0.jar
```
**Server runs on localhost:8001**
- Once started it should say:
```
Starting Server on port 8001
Type 'terminate' to close the server
```

- Connect client with telnet or nc
```
 nc localhost 8001
```

## Assumptions

- Chat rooms have a maximum of 10 users per room.
- Usernames assigned to new users have the following format: username + number from 0 to 10000.
- I used slf4j + logback libraries for logging purposes 
- I put some restrictions to the commands, if they are allowed when user is in a room or not.

## Application flow

From top to down:

- Chat Service takes responsability on PUB/SUB features, all of them are presented as commands
- CommandExecutor handles invocations on all registered commands, takes care too about execution thread pool
- Router handles client lifecycles and forward requests to CommandExecutor
- Client connections are represented as Sessions (User profiles), sessions are created once a user has valid
  username
- Socket connections are handled using Netty pipes:
    - String based messages are decoded to Commands
    - AuthHandler intercepts new connections and generates a different random username to authenticate 
      the connections
    - ClientHandler acts as pipe termination

## Threading Model

- Requests handled by Router::receiveMessage are moved by netty handlerGroup, so that, command execution is
  delegatedto its own thread pool ("serverExecutor"), isolating netty requests from command executions.

## Server Commands:
To stop the server you can type 'terminate' to the server console.
  ```
  terminate
  ```

## Client Commands:

- nick: set a nickname for user, if available. Client must not be in a room.
  ```
  /nick <nickname>
  ```
- join: try to join chat room (max 10 active clients per chat room). If chat room does not exist - create it
  first. If client’s limit exceeded - send an error, otherwise join chat room and send last N messages of
  activity. Server should support many chat rooms.
  ```
  /join <room_name>
  ```
- exit: disconnect client. Client must be in a room.
  ```
  /exit 
  ```
- list — show all chat rooms. Client must not be in a room.
  ```
  /list
  ```
- users — show users in a chat room. Client must be in a room.
  ```
  /users
  ```
- publish: text message terminated with CR - sends message to current channel. No need to type /publish once in
  a room you can chat directly. Client must be in a room.

## Next Steps
- New command to leave room and return to main lobby
- Stress test

## Sample:

Client A

```
 nc localhost 8001

 Welcome user01

 /join roomA

 Welcome to room roomA, there are 9 users connected.

 hello there

```

Client B

```
 nc localhost 8001

 Welcome user02

 /join roomA
 
 Welcome to room roomA, there are 10 users connected.

 general kenobi

```

[JDK 11]: https://jdk.java.net/11/
[Gradle 6.8]: https://docs.gradle.org/6.8/release-notes.html