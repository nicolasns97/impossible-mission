package com.sapienza.jimpossiblemission.model;

import java.awt.*;
import java.util.Arrays;

public class ElevatorModel {
    private final GameWorld gameWorld;
    private final int x, y, width, height, speed;
    private final int[] stops;
    private boolean moving;
    private Direction currentDirection;
    private boolean enabled;

    public ElevatorModel(GameWorld gameWorld, int[] stops, int speed, int x, int y, int width, int height) {
        this.gameWorld = gameWorld;
        this.stops = stops;
        Arrays.sort(this.stops);
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.moving = false;
        this.currentDirection = null;
    }

    public void startMoving(Direction direction) {
        if (!enabled) return;
        this.currentDirection = direction;
        this.moving = true;
    }

    public void update() {
        if (!moving || currentDirection == null) return;

        int nextWorldY = gameWorld.getWorldY();

        Integer nextStop = findNextStop();
        if (nextStop != null) {
            if (currentDirection == Direction.UP) {
                nextWorldY = Math.max(nextStop, nextWorldY - speed);
            } else {
                nextWorldY = Math.min(nextStop, nextWorldY + speed);
            }
        }

        gameWorld.setWorldY(nextWorldY);

        if (isAtStop()) {
            moving = false;
            currentDirection = null;
        }
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(x, y+height-32, width, 32);
    }

    public boolean Old(AgentModel agent) {
        int offset = 20;
        int agentCenter = agent.getX() + agent.getWidth() / 2;
        return agentCenter - offset > this.x && agentCenter + offset < this.x + this.width;
    }

    public boolean isAgentOnElevator(AgentModel agent) {
        int offset = 20;
        int agentCenter = agent.getX() + agent.getWidth() / 2;
        return agentCenter - offset > this.x && agentCenter + offset < this.x + this.width;
    }

    private Integer findNextStop() {
        int elevatorSnapPosition = y + height - 32;
        if (currentDirection == Direction.UP) {
            for (int i = stops.length - 1; i >= 0; i--) {
                int stopY = stops[i] - elevatorSnapPosition;
                if (stopY < gameWorld.getWorldY()) return stopY;
            }
        } else {
            for (int stop : stops) {
                int stopY = stop - elevatorSnapPosition;
                if (stopY > gameWorld.getWorldY()) return stopY;
            }
        }
        return null; // Nessuna fermata nella direzione
    }

    private boolean isAtStop() {
        int currentWorldY = gameWorld.getWorldY();
        int elevatorSnapPosition = y + height - 32;
        for (int stop : stops) {
            if (stop - currentWorldY == elevatorSnapPosition) return true;
        }
        return false;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isMoving() {
        return moving;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
