package com.sapienza.jimpossiblemission.model.state;
import com.sapienza.jimpossiblemission.model.*;

public class TerminalState implements GameState {
    private final GameModel model;

    public TerminalState(GameModel model) {
        this.model = model;
    }

    @Override
    public void up() {
        model.getTerminalMenu().up();
    }

    @Override
    public void down() {
        model.getTerminalMenu().down();
    }

    @Override
    public void space() {
        int selectResult = model.getTerminalMenu().select(model.getGameSession().getInventory());

        Room currentRoom = model.getCurrentRoom();
        if (selectResult == 0) {
            for (PlatformGroup platformGroup : currentRoom.getPlatformGroups()) {
                for (Platform platform : platformGroup.getPlatforms()) {
                    platform.resetPosition();
                }
            }
            currentRoom.getEnemies().forEach(Enemy::start);
        } else if (selectResult == 1) {
            model.setSnoozing(true);
        } else if (selectResult == 2 && !model.isSnoozing()) {
            currentRoom.getEnemies().forEach(Enemy::start);
        }
        model.getTerminalMenu().setActive(false);
        model.setState(model.getRoomState());
    }

    @Override
    public void update() {

    }
}