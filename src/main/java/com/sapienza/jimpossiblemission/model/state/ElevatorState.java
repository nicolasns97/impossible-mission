package com.sapienza.jimpossiblemission.model.state;

import com.sapienza.jimpossiblemission.model.Direction;
import com.sapienza.jimpossiblemission.model.GameModel;

public class ElevatorState implements GameState {
    private final GameModel model;

    public ElevatorState(GameModel model) {
        this.model = model;
    }

    @Override
    public void up() {
        // Se siamo qui, premiamo Su per far salire l'ascensore
        model.getElevator().startMoving(Direction.UP);
    }

    @Override
    public void down() {
        // Premiamo Giù per far scendere l'ascensore
        model.getElevator().startMoving(Direction.DOWN);
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
        // La barra spaziatrice fa saltare l'agente (se non è bloccato)
        if (model.getAgent().isEnabled()) {
            model.getAgent().pressJump();
        }
    }

    @Override
    public void update() {
        // Deleghiamo al GameModel la fisica dell'area ascensore
        model.updateElevatorLogic();
    }
}