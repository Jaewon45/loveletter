# Love Letter Card Game

A Java implementation of the Love Letter card game with network multiplayer support.

## Project Structure 

## Prerequisites

- Java
- Gradle for building and running the project

## Configuration

change in config.properties

## Building and Running

1. Build and startthe project: 

2. Run the server:
```bash
./gradlew server:run
```

3. Run the client (in a separate terminal for each player):
```bash
./gradlew client:run
```

## Running Tests


## Architecture

Server Architecture:

├── CardAction.java
├── Card.java
├── Deck.java
├── effect
│   ├── AssassinEffect.java
│   ├── BaronEffect.java
│   ├── BaronessEffect.java
│   ├── BishopEffect.java
│   ├── CardinalEffect.java
│   ├── ConstableEffect.java
│   ├── CountEffect.java
│   ├── CountessEffect.java
│   ├── DowagerQueenEffect.java
│   ├── Effect.java
│   ├── GuardEffect.java
│   ├── HandmaidEffect.java
│   ├── JesterEffect.java
│   ├── KingEffect.java
│   ├── PriestEffect.java
│   ├── PrinceEffect.java
│   ├── PrincessEffect.java
│   └── SycophantEffect.java
├── Game.java
├── Player.java
└── TCPServer.java

the server logic is only slightly decoupled from the game logic


