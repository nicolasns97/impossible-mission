package com.sapienza.jimpossiblemission.model;

import org.mapeditor.core.Map;
import org.mapeditor.core.MapObject;
import org.mapeditor.core.ObjectGroup;
import org.mapeditor.io.TMXMapReader;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class TMXReader {
    protected final TMXMapReader reader;

    public TMXReader(TMXMapReader reader) {
        this.reader = reader;
    }

    public static List<Rectangle> getRectanglesFromLayer(Map map, String layerName) {
        return map.getLayers().stream()
                .filter(layer -> layer instanceof ObjectGroup)
                .map(layer -> (ObjectGroup) layer)
                .filter(objectGroup -> layerName.equals(objectGroup.getName()))
                .findFirst()
                .map(objectGroup -> objectGroup.getObjects().stream()
                        .map(o -> new Rectangle(
                                (int) o.getX(),
                                (int) o.getY(),
                                o.getWidth().intValue(),
                                o.getHeight().intValue()
                        ))
                        .collect(Collectors.toList())
                )
                .orElseGet(ArrayList::new);
    }

    public static List<Position> getPositionsFromLayer(Map map, String layerName) {
        return map.getLayers().stream()
                .filter(layer -> layer instanceof ObjectGroup)
                .map(layer -> (ObjectGroup) layer)
                .filter(objectGroup -> layerName.equals(objectGroup.getName()))
                .findFirst()
                .map(objectGroup -> objectGroup.getObjects().stream()
                        .map(o -> new Position((int) o.getX(), (int) o.getY()))
                        .collect(Collectors.toList())
                )
                .orElseGet(ArrayList::new);
    }

    public static List<Position> getPositionsFromLayer(Map map, String layerName, Predicate<MapObject> filter) {
        return map.getLayers().stream()
                .filter(layer -> layer instanceof ObjectGroup)
                .map(layer -> (ObjectGroup) layer)
                .filter(objectGroup -> layerName.equals(objectGroup.getName()))
                .findFirst()
                .map(objectGroup -> objectGroup.getObjects().stream()
                        .filter(filter)
                        .map(o -> new Position((int) o.getX(), (int) o.getY()))
                        .collect(Collectors.toList())
                )
                .orElseGet(ArrayList::new);
    }
}
