package com.sapienza.jimpossiblemission.model;

import com.sapienza.jimpossiblemission.Direction;

import java.awt.*;
import java.util.List;
import java.util.Observable;

public class GameModel extends Observable {
    private final TileMapModel tileMap;
    private final GameWorld gameWorld;
    private final ElevatorModel elevator;
    private final AgentModel agent;
    private final CollisionManager collisionManager;

    public GameModel(TileMapModel tileMap, GameWorld gameWorld, ElevatorModel elevator, AgentModel agent, CollisionManager collisionManager) {
        this.tileMap = tileMap;
        this.gameWorld = gameWorld;
        this.elevator = elevator;
        this.agent = agent;
        this.collisionManager = collisionManager;
    }

    public void update() {
        // 1. Aggiorna tutti i modelli (fisica, movimento, animazioni)
        updateEntities();

        // 2. Risolvi interazioni/collisioni (player ↔ elevator, player ↔ mondo, ecc.)
        handleInteractions();

        // 3. Aggiorna logiche di gioco (elevatore, missioni, punteggio, ecc.)
        updateGameLogic();

        // 4. Notifica la view
        setChanged();
        notifyObservers();
    }

    private void updateEntities() {
        // 0. Recupero le aree solide
        List<Rectangle> collisionObjects = tileMap.getCollisionObjects(gameWorld.getWorldX(), gameWorld.getWorldY());
        collisionObjects.add(elevator.getBoundingBox());

        // 1. Gravità solo se non è a terra
        if (!agent.isOnGround()) {
            agent.applyGravity();
        }

        // 2. Movimento orizzontale
        int dx = 0;
        if (agent.isMoving()) {
            dx = agent.getDirection() == Direction.RIGHT ? agent.getSpeed() : -agent.getSpeed();
        }
        CollisionManager.CollisionResult colX = collisionManager.checkCollision(collisionObjects, agent.getBoundingBox(), dx, 0);
        if (!colX.collided) {
            agent.setX(agent.getX() + dx);
        } else {
            // ferma il movimento o eventualmente gestisci rimbalzo
        }

        // 3. Movimento verticale
        int dy = (int) agent.getVelocityY();
        if (Math.abs(dy) < 1) dy = 0; // epsilon: evita micro-oscillazioni

        CollisionManager.CollisionResult colY = collisionManager.checkCollision(collisionObjects, agent.getBoundingBox(), 0, dy);

        if (!colY.collided) {
            agent.setY(agent.getY() + dy);
            agent.setOnGround(false);
        } else {
            if (dy > 0) { // caduta
                // Snap preciso sopra al blocco su cui atterra
                agent.setY(colY.collider.y - agent.getHeight());
                agent.land(); // azzera velocità e imposta onGround = true
            } else if (dy < 0) { // salto che sbatte al soffitto
                agent.setY(agent.getY() + colY.intersection.height);
                agent.setVelocityY(0);
            }
        }

        // 4. Aggiorna l’elevatore
        elevator.update();
    }


    private void handleInteractions() {
        agent.setEnabled(!elevator.isMoving());
        elevator.setEnabled(elevator.isAgentOnElevator(agent));
    }

    private void updateGameLogic() {

    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public ElevatorModel getElevator() {
        return elevator;
    }

    public AgentModel getAgent() {
        return agent;
    }

    public CollisionManager getCollisionManager() {
        return collisionManager;
    }

    public TileMapModel getTileMap() {
        return tileMap;
    }
}
