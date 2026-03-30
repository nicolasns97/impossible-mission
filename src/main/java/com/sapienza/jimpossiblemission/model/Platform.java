package com.sapienza.jimpossiblemission.model;

import java.awt.*;

public class Platform extends Entity {
    private final int startX;
    private final int startY;
    private boolean enabled;
    private boolean moving;
    private Direction movingDirection;
    private final int speed = 2;

    public Platform(int x, int y, int width, int height) {
        super(x, y, width, height);
        startX = x;
        startY = y;
    }

    public void resetPosition() {
        x = startX;
        y = startY;
    }

    public void setMoving(Direction direction) {
        moving = true;
        movingDirection = direction;
    }

    public void update() {
        if (moving) {
            int dy = movingDirection == Direction.UP ? -speed : speed;
            y += dy;
        }
    }

    public void stop() {
        moving = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isMoving() {
        return moving;
    }

    public int getSpeed() {
        return speed;
    }

    public Direction getMovingDirection() {
        return movingDirection;
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, width, height);
    }


}
