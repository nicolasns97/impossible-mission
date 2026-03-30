package com.sapienza.jimpossiblemission.model;

import com.sapienza.jimpossiblemission.model.Room.RoomCode;
import org.mapeditor.core.MapObject;
import org.mapeditor.core.ObjectGroup;
import org.mapeditor.io.TMXMapReader;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ElevatorAreaModel {
    private final org.mapeditor.core.Map map;
    private final List<Rectangle> collisionObjects = new ArrayList<>();
    private final List<RoomTrigger> roomTriggers = new ArrayList<>();

    public ElevatorAreaModel(String tmxPath) {
        TMXMapReader reader;
        try {
            reader = new TMXMapReader();
            map = reader.readMap(tmxPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        loadCollisionObjects();
        loadRoomTriggers();
    }

    private void loadRoomTriggers() {
        ObjectGroup rooms = map.getLayers().stream()
                .filter(layer -> layer instanceof ObjectGroup)
                .map(layer -> (ObjectGroup) layer)
                .filter(objectGroup -> "rooms".equals(objectGroup.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Collision Layer non trovato"));
        for (MapObject object : rooms.getObjects()) {
            RoomTrigger roomTrigger = new RoomTrigger(
                    (int) object.getX(),
                    (int) object.getY(),
                    object.getProperties().getProperty("room-id"));
            roomTriggers.add(roomTrigger);
        }
    }

    private void loadCollisionObjects() {
        ObjectGroup collisionLayer = map.getLayers().stream()
                .filter(layer -> layer instanceof ObjectGroup)
                .map(layer -> (ObjectGroup) layer)
                .filter(objectGroup -> "solid-area".equals(objectGroup.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Collision Layer non trovato"));
        for (MapObject object : collisionLayer.getObjects()) {
            Rectangle rectObject = new Rectangle(
                    (int) object.getX(),
                    (int) object.getY(),
                    object.getWidth().intValue(),
                    object.getHeight().intValue());
            collisionObjects.add(rectObject);
        }
    }

    public org.mapeditor.core.Map getMap() {
        return map;
    }

    public int[] getElevatorStops() {
        return map.getLayers().stream()
                .filter(layer -> layer instanceof ObjectGroup)
                .map(layer -> (ObjectGroup) layer)
                .filter(objectGroup -> "elevator-stops".equals(objectGroup.getName()))
                .flatMap(objectGroup -> objectGroup.getObjects().stream())
                .mapToInt(mapObject -> (int) mapObject.getY())
                .toArray();
    }

    public Point getAgentSpawnPoint() {
        return map.getLayers().stream()
                .filter(layer -> layer instanceof ObjectGroup)
                .map(layer -> (ObjectGroup) layer)
                .filter(objectGroup -> "spawn".equals(objectGroup.getName()))
                .flatMap(objectGroup -> objectGroup.getObjects().stream())
                .findFirst()
                .map(mapObject -> new Point((int) mapObject.getX(), (int) mapObject.getY()))
                .orElseThrow(() -> new IllegalStateException("Spawn point non trovato"));
    }

    public List<Rectangle> getCollisionObjects() {
        return collisionObjects;
    }

    public List<Rectangle> getCollisionObjects(int dx, int dy) {
        return collisionObjects.stream()
                .map(r -> new Rectangle((int) (r.getX() - dx), (int) (r.getY() - dy), r.width, r.height))
                .collect(Collectors.toList());
    }

    public List<RoomTrigger> getRoomTriggers(int dx, int dy) {
        return roomTriggers.stream()
                .map(r -> new RoomTrigger(r.getX() - dx, r.getY() - dy, r.getCode()))
                .collect(Collectors.toList());
    }

    public RoomTrigger getRoomTrigger(RoomCode roomCode, int dx, int dy) {
        return roomTriggers.stream()
                .filter(p -> p.getCode().equals(roomCode))
                .map(p -> new RoomTrigger(p.getX() - dx, p.getY() - dy, p.getCode()))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }
}
