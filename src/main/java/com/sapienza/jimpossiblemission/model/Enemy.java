package com.sapienza.jimpossiblemission.model;

public abstract class Enemy extends Entity {
    private final int startX, startY;

    public Enemy(int x, int y, int width, int height) {
        super(x, y, width, height);
        startX = x;
        startY = y;
    }

    public abstract void update();
    public abstract void toggle();
    public void reset() {
        this.x = startX;
        this.y = startY;
    }
}
