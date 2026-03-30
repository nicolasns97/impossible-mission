package com.sapienza.jimpossiblemission.model;

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
    private boolean jumping;
    private boolean interacting;

    private float velocityY = 0;
    private float velocityX = 0;
    private final float gravity = 0.3f; // forza gravità
    private final float jumpStrength = -7f; // impulso salto
    private boolean onGround = true;

    private int jumpBufferFrames = 8;
    private int jumpBufferCounter = 0;

    public AgentModel(int x, int y, int width, int height, int speed, Direction direction) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.direction = direction;
        this.jumping = false;
        this.interacting = false;
    }

    public void applyGravity() {
        velocityY += gravity;
    }

    public void jump() {
        if (onGround) {
            velocityY = jumpStrength;
            onGround = false;
            jumping = true;

            int i = 2;
            if (direction == Direction.RIGHT) {
                velocityX = speed + i;
            } else {
                velocityX = -(speed+ i);
            }
        }
    }

    public void land() {
        velocityY = 0;
        onGround = true;
        jumping = false;
    }

    public Rectangle getBoundingBox() {
        int marginX = 20; // margine laterale
        int marginY = 0;  // margine superiore e inferiore
        int roofOffset = 0;
        if (jumping) {
            roofOffset = 50;
        }
        return new Rectangle(x + marginX, y + marginY + roofOffset, width - 2 * marginX, height - roofOffset);
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

    public boolean isJumping() {
        return jumping;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public void updateJump() {
        if (jumpBufferCounter > 0) {
            if (onGround) {
                jump(); // parte il salto
                jumpBufferCounter = 0;
            } else {
                jumpBufferCounter--; // attendo che tocchi terra
            }
        }
    }

    public void pressJump() {
        jumpBufferCounter = jumpBufferFrames;
    }

    public boolean isInteracting() {
        return this.interacting;
    }

    public void startInteracting() {
        this.interacting = true;
    }

    public void stopInteracting() {
        this.interacting = false;
    }
}
