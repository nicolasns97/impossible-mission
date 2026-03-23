package com.sapienza.jimpossiblemission.model;

import org.mapeditor.core.Map;
import org.mapeditor.io.TMXMapReader;

import java.awt.*;
import java.util.List;

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
            List<Rectangle> containers = getRectanglesFromLayer(map, "containers");
            List<Position> enemySpawnPositions = getPositionsFromLayer(map, "enemies");
            Position spawn = getPositionsFromLayer(map, "spawn").get(0);
            Position exit = getPositionsFromLayer(map, "exit").get(0);
            roomMetadata = new RoomMetadata(collisionObjects, platforms, platformsStops, containers, enemySpawnPositions, spawn, exit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return roomMetadata;
    }


    public static class RoomMetadata {
        private final List<Rectangle> collisionObjects;
        private final List<Position> platforms;
        private final List<Position> platformsStops;
        private final List<Rectangle> containers;
        private final List<Position> enemySpawnPositions;
        private final Position spawnPosition;
        private final Position exit;

        public RoomMetadata(List<Rectangle> collisionObjects, List<Position> platforms, List<Position> platformsStops, List<Rectangle> containers, List<Position> enemySpawnPositions, Position spawnPosition, Position exit) {
            this.collisionObjects = collisionObjects;
            this.platforms = platforms;
            this.platformsStops = platformsStops;
            this.containers = containers;
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

        public List<Rectangle> getContainers() {
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
    }
}
