package com.sapienza.jimpossiblemission.model;

import com.sapienza.jimpossiblemission.model.physics.CollisionManager;
import com.sapienza.jimpossiblemission.model.physics.CollisionManager.CollisionResult;

public class InteractionManager {
    private final CollisionManager collisionManager;

    public InteractionManager(CollisionManager collisionManager) {
        this.collisionManager = collisionManager;
    }

    /**
     * Controlla se l'agente sta collidendo con un oggetto interagibile nella stanza.
     * Restituisce l'oggetto trovato, oppure null se non c'è nulla.
     */
    public Interactable checkInteractions(AgentModel agent, Room currentRoom) {
        if (currentRoom == null) return null;

        // 1. Controlla le casse (Containers) non ancora completate
        for (Container container : currentRoom.getContainers()) {
            if (container.getSearchCompletion() >= 100) continue;

            CollisionResult collisionResult = collisionManager.checkCollision(container.getBoundingBox(), agent.getBoundingBox());
            if (collisionResult.collided) {
                return container; // Trovata una cassa!
            }
        }

        // 2. Controlla il Terminale (se presente nella stanza)
        if (currentRoom.getTerminal().isPresent()) {
            Terminal terminal = currentRoom.getTerminal().get();
            CollisionResult collisionResult = collisionManager.checkCollision(terminal.getBoundingBox(), agent.getBoundingBox());
            if (collisionResult.collided) {
                return terminal; // Trovato il terminale!
            }
        }

        // Nessuna interazione trovata
        return null;
    }
}