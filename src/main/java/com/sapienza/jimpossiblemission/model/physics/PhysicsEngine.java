package com.sapienza.jimpossiblemission.model.physics;

import com.sapienza.jimpossiblemission.model.AgentModel;
import com.sapienza.jimpossiblemission.model.Direction;
import com.sapienza.jimpossiblemission.model.physics.CollisionManager.CollisionResult;

import java.awt.Rectangle;
import java.util.List;

public class PhysicsEngine {
    private final CollisionManager collisionManager;

    public PhysicsEngine(CollisionManager collisionManager) {
        this.collisionManager = collisionManager;
    }

    public void applyPhysics(AgentModel agent, List<Rectangle> collisionObjects) {
        // 1. Salto e Gravità
        agent.updateJump();
        if (!agent.isOnGround()) {
            agent.applyGravity();
        }

        // 2. Movimento X
        int inputDx = 0;
        if (agent.isMoving()) {
            inputDx = agent.getDirection() == Direction.RIGHT ? agent.getSpeed() : -agent.getSpeed();
        }
        if (agent.isJumping()) {
            inputDx = (int) agent.getVelocityX();
        }

        CollisionResult colX = collisionManager.checkCollision(collisionObjects, agent.getBoundingBox(), inputDx, 0);
        if (!colX.collided) {
            agent.setX(agent.getX() + inputDx);
        } else {
            agent.setVelocityX(0); // Ferma l'inerzia orizzontale
        }

        // 3. Movimento Y
        int dy = (int) agent.getVelocityY();
        if (Math.abs(dy) < 1) dy = 0;

        CollisionResult colY = collisionManager.checkCollision(collisionObjects, agent.getBoundingBox(), 0, dy);
        if (!colY.collided) {
            agent.setY(agent.getY() + dy);
            agent.setOnGround(false);
        } else {
            if (dy > 0) { // Atterraggio
                agent.setY(colY.collider.y - agent.getHeight());
                agent.land();
                agent.setVelocityX(0);
            } else if (dy < 0) { // Testata sul soffitto
                agent.setY(agent.getY() + colY.intersection.height);
                agent.setVelocityY(0);
            }
        }
    }
}
