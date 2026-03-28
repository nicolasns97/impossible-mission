package com.sapienza.jimpossiblemission.model;

import com.sapienza.jimpossiblemission.Direction;

import java.awt.*;

public class PatrollingRobot extends Enemy {
    private final int speed = 2;
    private Direction direction;
    private boolean enabled;

    public PatrollingRobot(int x, int y, int width, int height, Direction direction) {
        super(x, y-height, width, height);
        this.enabled = true;
        this.direction = direction;
    }

    public PatrollingRobot(int x, int y, Direction direction) {
        this(x, y, 75, 75, direction);
    }

    @Override
    public void update() {
        if (!enabled) return;

        int dx = direction == Direction.RIGHT ? speed : -speed;
        x += dx;
    }

    @Override
    public void toggle() {
        enabled = !enabled;
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, width, height);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getSpeed() {
        return speed;
    }
}
