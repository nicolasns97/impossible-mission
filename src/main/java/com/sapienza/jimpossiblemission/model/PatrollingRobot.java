package com.sapienza.jimpossiblemission.model;

import java.awt.*;

public class PatrollingRobot extends Enemy {
    private final int speed = 2;

    public PatrollingRobot(int x, int y, int width, int height, Direction direction) {
        super(x, y-height, width, height, direction);
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
