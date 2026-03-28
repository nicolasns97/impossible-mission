package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.model.*;
import com.sapienza.jimpossiblemission.model.Container;
import com.sapienza.jimpossiblemission.model.Room.RoomCode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RoomView {
    private Room room;
    private final Map<RoomCode, BufferedImage> roomBackgroundMap;
    private final Map<RoomCode, BufferedImage> platformSpriteMap;
    private List<RobotView> robots;
    private List<ContainerView> containers;
    private TerminalView terminal;


    public RoomView(Map<RoomCode, BufferedImage> roomBackgroundMap, Map<RoomCode, BufferedImage> platformSpriteMap) {
        this.roomBackgroundMap = roomBackgroundMap;
        this.platformSpriteMap = platformSpriteMap;
    }

    public void draw(Graphics2D g) {
        List<Position> platforms = room.getPlatformGroups().stream()
                .flatMap(platformGroup -> platformGroup.getPlatforms().stream())
                .map(Entity::getPosition)
                .collect(Collectors.toList());
        g.drawImage(roomBackgroundMap.get(room.getCode()), 0, 0, null);
        for (Position platform : platforms) {
            BufferedImage platformSprite = platformSpriteMap.get(room.getCode());
            g.drawImage(platformSprite, platform.getX(), platform.getY(), platformSprite.getWidth(), platformSprite.getHeight(), null);
        }
        if (containers != null) {
            containers.forEach(container -> container.draw(g));
        }
        if (terminal != null) {
            terminal.draw(g);
        }
        if (robots != null) {
            robots.forEach(robot -> robot.draw(g));
        }
    }

    public void setRoom(Room room) {
        this.room = room;
        this.containers = room.getContainers().stream().map(ContainerView::new).collect(Collectors.toList());
        this.terminal = room.getTerminal().map(TerminalView::new).orElse(null);
        this.robots = room.getEnemies().stream().map(enemy -> new RobotView((PatrollingRobot) enemy)).collect(Collectors.toList());
    }
}
