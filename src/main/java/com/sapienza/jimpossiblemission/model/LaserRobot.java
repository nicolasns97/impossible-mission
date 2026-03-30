package com.sapienza.jimpossiblemission.model;

import java.awt.Rectangle;
import java.util.List;

public class LaserRobot extends Enemy{
    private int frameCounter;
    private final int FIRE_COOLDOWN = 120;
    private final int FIRE_DURATION = 60;
    private boolean firing = false;

    public LaserRobot(int x, int y, int width, int height, Direction direction) {
        super(x, y - height, width, height, direction);
        frameCounter = 0;
    }

    public LaserRobot(int x, int y, Direction direction) {
        this(x, y, 75, 75, direction);
    }

    @Override
    public void update(List<Rectangle> roomSolids) {
        frameCounter++;
        if (!firing && frameCounter >= FIRE_COOLDOWN) {
            startFiring();
        } else if (firing && frameCounter >= FIRE_DURATION) {
            stopFiring();
        }
    }

    private void startFiring() {
        if (enabled) {
            firing = true;
            frameCounter = 0;
        }
    }

    private void stopFiring() {
        firing = false;
        frameCounter = 0;
    }

    public boolean isFiring() {
        return firing;
    }


    @Override
    public Rectangle getBoundingBox() {
        if (firing) {
            if (direction.equals(Direction.LEFT)) {
                return new Rectangle(x-140, y, width+140, height);
            } else {
                return new Rectangle(x, y,  65 + 150, height);
            }
        } else {
            return new Rectangle(x, y, width, height);
        }
    }
}
