package com.sapienza.jimpossiblemission.model;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Room {
    private final RoomCode code;
    private final Position spawnPosition;
    private final Position exit;
    private final List<Rectangle> collisionObjects;
    private final List<Enemy> enemies;
    private final List<Container> containers;
    private final Terminal terminal;
    private final List<PlatformGroup> platformGroups;

    public Room(RoomCode code,
                Position spawnPosition,
                Position exit,
                List<Rectangle> collisionObjects,
                List<Enemy> enemies,
                List<Container> containers,
                Terminal terminal,
                List<PlatformGroup> platformGroups) {
        this.code = code;
        this.spawnPosition = spawnPosition;
        this.exit = exit;
        this.collisionObjects = collisionObjects;
        this.enemies = enemies;
        this.containers = containers;
        this.terminal = terminal;
        this.platformGroups = platformGroups;
    }

    public RoomCode getCode() {
        return code;
    }

    public Position getSpawnPosition() {
        return spawnPosition;
    }

    public List<Rectangle> getCollisionObjects() {
        List<Rectangle> collisionObjects = new ArrayList<>();
        List<Rectangle> platforms = platformGroups.stream()
                .flatMap(p -> p.getPlatforms().stream()).map(Platform::getBoundingBox)
                .collect(Collectors.toList());
        collisionObjects.addAll(platforms);
        collisionObjects.addAll(this.collisionObjects);
        return collisionObjects;
    }

    public List<Rectangle> getStaticCollisionObjects() {
        return this.collisionObjects;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public Optional<Terminal> getTerminal() {
        return Optional.ofNullable(terminal);
    }

    public List<PlatformGroup> getPlatformGroups() {
        return platformGroups;
    }

    public Position getExit() {
        return exit;
    }

    public enum RoomCode {
        ECHO_CHAMBER("echo-chamber"),
        SHADOW_VAULT("shadow-vault"),
        SILICON_LAB("silicon-lab"),
        SENTRY_HALL("sentry-hall"),
        DATA_GRID_ROOM("data-grid-room"),
        MECH_CORRIDOR("mech-corridor"),
        CONTROL_NODE("control-node"),
        CORE_TERMINAL("core-terminal");

        private final String path;
        private static final Map<String, RoomCode> BY_PATH = new HashMap<>();
        static {
            for (RoomCode r : values()) {
                BY_PATH.put(r.path, r);
            }
        }
        RoomCode(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public static RoomCode fromPath(String path) {
            return BY_PATH.get(path);
        }
    }
}
