package com.sapienza.jimpossiblemission.model;


import java.awt.Rectangle;
import java.util.List;

public class PatrollingRobot extends Enemy {
    private final int speed = 2;

    public PatrollingRobot(int x, int y, int width, int height, Direction direction) {
        super(x, y-height, width, height, direction);
    }

    public PatrollingRobot(int x, int y, Direction direction) {
        this(x, y, 75, 75, direction);
    }

    @Override
    public void update(List<Rectangle> roomSolids) {
        if (!enabled) return;

        // 1. Sensore "salva-vita" per non cadere dalle piattaforme
        int sensorX = (direction == Direction.RIGHT) ? x + width + 5 : x - 15;
        Rectangle sensorDown = new Rectangle(sensorX, y + height, 1, 10);

        // 2. Controllo del pavimento
        boolean floorAhead = roomSolids != null && roomSolids.stream().anyMatch(solid -> solid.intersects(sensorDown));

        // 3. Logica di movimento
        if (!floorAhead) {
            invertDirection();
        } else {
            int dx = direction == Direction.RIGHT ? speed : -speed;
            x += dx;
        }
    }

    public void invertDirection() {
        super.direction = (direction == Direction.LEFT) ? Direction.RIGHT : Direction.LEFT;
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, width, height);
    }


    public int getSpeed() {
        return speed;
    }
}
