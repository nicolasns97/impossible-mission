package com.sapienza.jimpossiblemission.factory;

import com.sapienza.jimpossiblemission.model.Platform;
import com.sapienza.jimpossiblemission.model.PlatformGroup;
import com.sapienza.jimpossiblemission.model.Position;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlatformFactory {
    private static final int PLATFORM_WIDTH = 94;
    private static final int PLATFORM_HEIGHT = 24;

    public static List<PlatformGroup> createPlatformGroups(List<Position> platformSpawnPosition, List<Position> stops) {
        Map<Integer, List<Platform>> groupedByX = new HashMap<>();

        for (Position position : platformSpawnPosition) {
            Platform platform = new Platform(position.getX(), position.getY(), PLATFORM_WIDTH, PLATFORM_HEIGHT);
            groupedByX.computeIfAbsent(position.getX(), k -> new ArrayList<>()).add(platform);
        }

        List<PlatformGroup> platformGroups = new ArrayList<>();
        for (Map.Entry<Integer, List<Platform>> entry : groupedByX.entrySet()) {
            int x = entry.getKey();
            List<Platform> platforms = entry.getValue();

            List<Position> stopsForX = stops.stream()
                    .filter(stop -> stop.getX() == x)
                    .collect(Collectors.toList());

            platformGroups.add(new PlatformGroup(platforms, stopsForX));
        }

        return platformGroups;
    }
}
