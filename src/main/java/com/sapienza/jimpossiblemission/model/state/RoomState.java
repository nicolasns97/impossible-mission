package com.sapienza.jimpossiblemission.model.state;

import com.sapienza.jimpossiblemission.model.*;
import java.util.Optional;

public class RoomState implements GameState {
    private final GameModel model;

    public RoomState(GameModel model) {
        this.model = model;
    }

    @Override
    public void up() {
        // 1. Muove le piattaforme
        model.movePlatform(Direction.UP);

        // 2. Gestisce l'interazione con gli oggetti
        Interactable currentInteractable = model.getCurrentInteractable();

        if (currentInteractable != null) {
            model.getAgent().startInteracting();
            currentInteractable.interact();

            // Interazione con un Contenitore (Cassa/Mobile)
            if (currentInteractable instanceof Container) {
                Container container = (Container) currentInteractable;
                Optional<Collectable> collectable = container.collect();

                if (collectable.isPresent()) {
                    model.setCollecting(true);
                    model.setCollectable(collectable.get());
                    model.getGameSession().getInventory().add(collectable.get());
                } else {
                    model.setCollecting(false);
                    model.setCollectingFrameCounter(0);
                }
            }
            // Interazione con un Terminale
            else if (currentInteractable instanceof Terminal) {
                // Ferma i nemici
                model.getCurrentRoom().getEnemies().forEach(Enemy::stop);
                // Attiva il menu
                model.getTerminalMenu().setActive(true);

                // TRANSIZIONE DI STATO: Il giocatore entra nel menu del terminale!
                model.setState(model.getTerminalState());
            }
        }
    }

    @Override
    public void down() {
        model.movePlatform(Direction.DOWN);
    }

    @Override
    public void left() {
        if (model.getAgent().isEnabled()) {
            model.getAgent().setDirection(Direction.LEFT);
            model.getAgent().setMoving(true);
        }
    }

    @Override
    public void right() {
        if (model.getAgent().isEnabled()) {
            model.getAgent().setDirection(Direction.RIGHT);
            model.getAgent().setMoving(true);
        }
    }

    @Override
    public void space() {
        // Salto dell'agente
        if (model.getAgent().isEnabled()) {
            model.getAgent().pressJump();
        }
    }

    @Override
    public void update() {
        // Delega al modello l'aggiornamento della fisica specifica della stanza.
        // Potrai dividere updateEntities() di GameModel in:
        // - model.updateRoomPhysics()
        // - model.handleRoomInteractions()
        model.updateRoomLogic();
    }
}