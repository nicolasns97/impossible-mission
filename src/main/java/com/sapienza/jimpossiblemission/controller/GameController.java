package com.sapienza.jimpossiblemission.controller;

import com.sapienza.jimpossiblemission.Direction;
import com.sapienza.jimpossiblemission.model.AgentModel;
import com.sapienza.jimpossiblemission.model.GameModel;
import com.sapienza.jimpossiblemission.view.GameView;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameController extends AbstractGameController {
    private final GameModel gameModel;
    private final GameView gameView;

    public GameController(GameModel gameModel, GameView gameView) {
        super(60);
        this.gameModel = gameModel;
        this.gameView = gameView;
        gameModel.addObserver(gameView);
        gameView.addKeyListener(new KeyAdapter() {
            private final AgentModel agent = gameModel.getAgent();
            private boolean leftPressed = false;
            private boolean rightPressed = false;

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> {
                        if (!agent.isEnabled()) break;

                        leftPressed = true;
                        agent.setMoving(true);
                        agent.setDirection(Direction.LEFT);
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (!agent.isEnabled()) break;

                        rightPressed = true;
                        agent.setMoving(true);
                        agent.setDirection(Direction.RIGHT);
                    }
                    case KeyEvent.VK_UP -> gameModel.getElevator().startMoving(Direction.UP);
                    case KeyEvent.VK_DOWN -> gameModel.getElevator().startMoving(Direction.DOWN);
                    case KeyEvent.VK_SPACE -> {
                        if (!agent.isEnabled()) break;
                        agent.pressJump();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> leftPressed = false;
                    case KeyEvent.VK_RIGHT -> rightPressed = false;
                }
                if (!leftPressed && !rightPressed) {
                    agent.setMoving(false);
                }
            }
        });
    }

    @Override
    protected void onFrame() {
        gameModel.update();
        gameView.repaint();
    }

    public GameView getGameView() {
        return gameView;
    }
}
