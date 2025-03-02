# Love Letter Card Game

A Java implementation of the Love Letter card game with network multiplayer support.

## Project Structure 

## Prerequisites

- Java 17 or higher
- Maven for building and running the project

## Building and Running

1. Build and test the project: 

2. Run the server:
```bash
mvn exec:java -Dexec.mainClass="com.example.loveletter.TCPServer"
```

3. Run the client (in a separate terminal):
```bash
mvn exec:java -Dexec.mainClass="com.example.loveletter.TCPClient"
```

## Running Tests

Run the test suite:
```bash
mvn test
```