# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Compile
mvn clean compile

# Build executable JAR
mvn package

# Run
java -jar target/jimpossiblemission-1.0-SNAPSHOT.jar

# Or run directly via Maven
mvn exec:java -Dexec.mainClass="com.sapienza.jimpossiblemission.Main"
```

No test suite is configured. Java 14+ required (OpenJDK 17 is available and compatible).

## Architecture

This is a 2D platformer game using a **classic MVC pattern** with Swing for rendering and a fixed 60 FPS game loop.

### MVC Wiring

`Main` → `GameFactory` wires together:
- `GameController` (extends `AbstractGameController`) — owns the game loop thread, handles keyboard input
- `GameModel` (extends `Observable`) — all game state and physics simulation
- `GameView` (extends `JPanel`, implements `Observer`) — rendering

`AbstractGameController` implements a Template Method pattern: the fixed-timestep loop calls the abstract `onFrame()` hook, which `GameController` overrides to call `gameModel.update()` then `gameView.repaint()`. `GameView` also receives updates via the Observer pattern when `GameModel` notifies after each tick.

### Model Layer

`GameModel` is the central hub. Each `update()` call:
1. Updates entities (physics, animations)
2. Handles interactions (collisions, state changes)
3. Updates game logic (win/lose conditions)

Key model classes:
- `AgentModel` — player physics: gravity, velocity, jump buffering (8-frame buffer), bounding box adjustments
- `Room` — level container holding collision objects, enemies, containers, terminals, and platform groups
- `CollisionManager` — AABB collision detection; returns collision flag, colliding rect, and intersection rect
- `PlatformGroup` / `Platform` — moving platforms with named stops; agent rides platforms when standing on them
- `TileMapModel` / `TMXReader` / `RoomMetadataReader` — parse `.tmx` Tiled map files (via `libtiled` dependency) to build rooms
- `GameSession` — tracks collectables (cards, codes) and game progress
- `PatrollingRobot` (extends `Enemy` extends `Entity`) — reverses direction every 120 frames

### View Layer

- `GameView` — top-level Swing panel, 1280×640 px; delegates to sub-views
- `AgentView` — sprite animation: 16-frame run cycle, 12-frame jump cycle, 4-tick delay per frame; flips sprites for left-facing direction
- `RoomView` — renders room background and platforms
- `RobotView` — renders patrolling robot sprites
- `ElevatorView` / `TileMapView` — elevator and tile map rendering

### Factory Package

`GameFactory` — wires MVC components together
`EnemyFactory` — creates `PatrollingRobot` instances from room metadata
`PlatformFactory` — creates `Platform`/`PlatformGroup` instances from TMX data

### Game Controls (for testing/debugging)

- LEFT/RIGHT arrows: move agent
- UP/DOWN arrows: move elevator/platform
- SPACE: jump (8-frame input buffer)
