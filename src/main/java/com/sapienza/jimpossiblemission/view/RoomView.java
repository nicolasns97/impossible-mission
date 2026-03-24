package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.model.Entity;
import com.sapienza.jimpossiblemission.model.PatrollingRobot;
import com.sapienza.jimpossiblemission.model.Position;
import com.sapienza.jimpossiblemission.model.Room;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RoomView {
    private Room room;
    private BufferedImage roomImage;
    private BufferedImage platformImage;
    private List<RobotView> robots;

    public RoomView() {
        try {
            roomImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/room/echo-chamber.png")));
            platformImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/platform/platform-yellow.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void draw(Graphics2D g) {
        List<Position> platforms = room.getPlatformGroups().stream()
                .flatMap(platformGroup -> platformGroup.getPlatforms().stream())
                .map(Entity::getPosition)
                .collect(Collectors.toList());
        g.drawImage(roomImage, 0, 0, null);
        for (Position platform : platforms) {
            g.drawImage(platformImage, platform.getX(), platform.getY(), platformImage.getWidth(), platformImage.getHeight(), null);
        }
        if (robots != null) {
            robots.forEach(robot -> robot.draw(g));
        }
    }

    public void setRoom(Room room) {
        this.room = room;
        this.robots = room.getEnemies().stream().map(enemy -> new RobotView((PatrollingRobot) enemy)).collect(Collectors.toList());
    }
}
