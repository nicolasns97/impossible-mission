package com.sapienza.jimpossiblemission.model;

import org.mapeditor.core.Map;
import org.mapeditor.core.ObjectGroup;
import org.mapeditor.io.TMXMapReader;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            List<Position> enemySpawnPositions = getPositionsFromLayer(map, "enemies");
            Position spawn = getPositionsFromLayer(map, "spawn").get(0);
            Position exit = getPositionsFromLayer(map, "exit").get(0);
            Terminal terminal = getTerminalFromLayer(map);
            roomMetadata = new RoomMetadata(collisionObjects, platforms, platformsStops, containers, terminal, enemySpawnPositions, spawn, exit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return roomMetadata;
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
                                Container.ContainerType.valueOf(o.getProperties().getProperty("type"))
                        ))
                        .collect(Collectors.toList())
                )
                .orElseGet(ArrayList::new);
    }

    public static class RoomMetadata {
        private final List<Rectangle> collisionObjects;
        private final List<Position> platforms;
        private final List<Position> platformsStops;
        private final List<Container> containers;
        private final Terminal terminal;
        private final List<Position> enemySpawnPositions;
        private final Position spawnPosition;
        private final Position exit;

        public RoomMetadata(List<Rectangle> collisionObjects, List<Position> platforms, List<Position> platformsStops, List<Container> containers, Terminal terminal, List<Position> enemySpawnPositions, Position spawnPosition, Position exit) {
            this.collisionObjects = collisionObjects;
            this.platforms = platforms;
            this.platformsStops = platformsStops;
            this.containers = containers;
            this.terminal = terminal;
            this.enemySpawnPositions = enemySpawnPositions;
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

        public List<Position> getEnemySpawnPositions() {
            return enemySpawnPositions;
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
