package com.sapienza.jimpossiblemission.model;

import com.sapienza.jimpossiblemission.Direction;

import java.awt.*;

public class AgentModel {
    private int x;
    private int y;
    private int width;
    private int height;
    private int speed;
    private Direction direction;
    private boolean moving;
    private boolean enabled;

    private float velocityY = 0;
    private final float gravity = 1.2f; // forza gravità
    private final float jumpStrength = -18f; // impulso salto
    private boolean onGround = false;

    public AgentModel(int x, int y, int width, int height, int speed, Direction direction) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.direction = direction;
    }

    public void applyGravity() {
        velocityY += gravity;
    }

    public void jump() {
        if (onGround) {
            velocityY = jumpStrength;
            onGround = false;
        }
    }

    public void land() {
        velocityY = 0;
        onGround = true;
    }


    public Rectangle getBoundingBox() {
        int marginX = 20; // margine laterale
        int marginY = 0;  // margine superiore e inferiore
        return new Rectangle(x + marginX, y + marginY, width - 2 * marginX, height - 2 * marginY);
    }

    public boolean isMoving() {
        return moving;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
