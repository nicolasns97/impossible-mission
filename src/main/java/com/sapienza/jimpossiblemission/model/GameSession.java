package com.sapienza.jimpossiblemission.model;

import java.io.Serializable;
import java.util.List;

public class GameSession implements Serializable {
    private final Inventory inventory;

    private final AgentModel agent;
    private final List<Room> rooms;
    private final List<Life> lives;
    private final GameTimer timer;

    public GameSession(Inventory inventory, AgentModel agent, List<Room> rooms, List<Life> lives, GameTimer timer) {
        this.inventory = inventory;
        this.agent = agent;
        this.rooms = rooms;
        this.lives = lives;
        this.timer = timer;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public AgentModel getAgent() {
        return agent;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Life> getLives() {
        return lives;
    }

    public GameTimer getTimer() {
        return timer;
    }
}
