package com.sapienza.jimpossiblemission.model;

import org.mapeditor.core.Map;
import org.mapeditor.core.MapObject;
import org.mapeditor.core.ObjectGroup;
import org.mapeditor.io.TMXMapReader;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomMetadataReader extends TMXReader {

    public RoomMetadataReader(TMXMapReader reader) {
        super(reader);
    }

    public RoomMetadata readRoomMetadata(String path) {
        RoomMetadata roomMetadata;
        try {
            Map map = super.reader.readMap(path);
            List<Rectangle> collisionObjects = getRectanglesFromLayer(map, "collision-objects");
            List<Position> platforms = getPositionsFromLayer(map, "platforms-stops", o -> o.getProperties().getProperty("spawn") != null);
            List<Position> platformsStops = getPositionsFromLayer(map, "platforms-stops");
            List<Container> containers = getContainersFromLayer(map);
            List<Enemy> enemySpawnPositions = getEnemiesFromLayer(map);
            Position spawn = getPositionsFromLayer(map, "spawn").get(0);
            Position exit = getPositionsFromLayer(map, "exit").get(0);
            Terminal terminal = getTerminalFromLayer(map);
            roomMetadata = new RoomMetadata(collisionObjects, platforms, platformsStops, containers, terminal, enemySpawnPositions, spawn, exit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return roomMetadata;
    }

    private List<Enemy> getEnemiesFromLayer(Map map) {
        return map.getLayers().stream()
                .filter(layer -> layer instanceof ObjectGroup)
                .map(layer -> (ObjectGroup) layer)
                .filter(objectGroup -> "enemies".equals(objectGroup.getName()))
                .findFirst()
                .map(objectGroup -> objectGroup.getObjects().stream()
                        .map(this::mapToEnemy)
                        .collect(Collectors.toList())
                )
                .orElseGet(ArrayList::new);
    }

    private Enemy mapToEnemy(MapObject object) {
        String type = object.getProperties().getProperty("type");
        String direction = object.getProperties().getProperty("direction");
        if (type.equals("PATROLLING")) {
            return new PatrollingRobot((int) object.getX(),
                    (int) object.getY(), Direction.valueOf(direction));
        } else if (type.equals("LASER")) {
            return new LaserRobot((int) object.getX(),
                    (int) object.getY(), Direction.valueOf(direction));
        } else {
            throw new IllegalStateException();
        }
    }

    private Terminal getTerminalFromLayer(Map map) {
        return map.getLayers().stream()
                .filter(layer -> layer instanceof ObjectGroup)
                .map(layer -> (ObjectGroup) layer)
                .filter(og -> "terminal".equals(og.getName()))
                .findFirst()
                .flatMap(og -> og.getObjects().stream().findFirst())
                .map(o -> new Terminal((int) o.getX(), (int) o.getY(), 100, 100))
                .orElse(null);
    }

    public static List<Container> getContainersFromLayer(Map map) {
        return map.getLayers().stream()
                .filter(layer -> layer instanceof ObjectGroup)
                .map(layer -> (ObjectGroup) layer)
                .filter(objectGroup -> "containers".equals(objectGroup.getName()))
                .findFirst()
                .map(objectGroup -> objectGroup.getObjects().stream()
                        .map(o -> new Container(
                                (int) o.getX(),
                                (int) o.getY(),
                                Container.ContainerType.valueOf(o.getProperties().getProperty("type")),
                                mapCollectable(o.getProperties().getProperty("collectable"))
                        ))
                        .collect(Collectors.toList())
                )
                .orElseGet(ArrayList::new);
    }

    private static Collectable mapCollectable(String label) {
        Collectable collectable;
        switch (label) {
            case "CARD" -> collectable = new Card();
            case "LIFT" -> collectable = new Code(Code.CodeType.PLATFORM_RESET);
            case "SNOOZE" -> collectable = new Code(Code.CodeType.SNOOZE);
            case "KEY" -> collectable = new Code(Code.CodeType.DOOR_KEY);
            default -> throw new IllegalStateException();
        }
        return collectable;
    }

    public static class RoomMetadata {
        private final List<Rectangle> collisionObjects;
        private final List<Position> platforms;
        private final List<Position> platformsStops;
        private final List<Container> containers;
        private final Terminal terminal;
        private final List<Enemy> enemies;
        private final Position spawnPosition;
        private final Position exit;

        public RoomMetadata(List<Rectangle> collisionObjects, List<Position> platforms, List<Position> platformsStops, List<Container> containers, Terminal terminal, List<Enemy> enemies, Position spawnPosition, Position exit) {
            this.collisionObjects = collisionObjects;
            this.platforms = platforms;
            this.platformsStops = platformsStops;
            this.containers = containers;
            this.terminal = terminal;
            this.enemies = enemies;
            this.spawnPosition = spawnPosition;
            this.exit = exit;
        }

        public List<Rectangle> getCollisionObjects() {
            return collisionObjects;
        }

        public List<Position> getPlatforms() {
            return platforms;
        }

        public List<Container> getContainers() {
            return containers;
        }

        public List<Enemy> getEnemies() {
            return enemies;
        }

        public Position getSpawnPosition() {
            return spawnPosition;
        }

        public List<Position> getPlatformsStops() {
            return platformsStops;
        }

        public Position getExit() {
            return exit;
        }

        public Terminal getTerminal() {
            return terminal;
        }
    }
}
