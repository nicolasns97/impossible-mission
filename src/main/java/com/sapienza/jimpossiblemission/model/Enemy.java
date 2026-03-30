package com.sapienza.jimpossiblemission.model;

public abstract class Enemy extends Entity {
    private final int startX, startY;
    Direction direction;
    boolean enabled;

    public Enemy(int x, int y, int width, int height, Direction direction) {
        super(x, y, width, height);
        this.direction = direction;
        this.enabled = true;
        startX = x;
        startY = y;
    }

    public abstract void update();

    public void stop() {
        this.enabled = false;
    };

    public void start() {
        this.enabled = true;
    }
    public void reset() {
        this.x = startX;
        this.y = startY;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }


}
