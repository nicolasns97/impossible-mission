package com.sapienza.jimpossiblemission.controller;

import com.sapienza.jimpossiblemission.model.Direction;
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
            private boolean leftPressed = false;
            private boolean rightPressed = false;

            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> {
                        leftPressed = true;
                        gameModel.left();
                    }
                    case KeyEvent.VK_RIGHT -> {
                        rightPressed = true;
                        gameModel.right();
                    }
                    case KeyEvent.VK_UP -> gameModel.up();
                    case KeyEvent.VK_DOWN -> gameModel.down();
                    case KeyEvent.VK_SPACE -> gameModel.space();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> leftPressed = false;
                    case KeyEvent.VK_RIGHT -> rightPressed = false;
                    case KeyEvent.VK_UP -> gameModel.stopInteracting();
                }

                if (!leftPressed && !rightPressed) {
                    gameModel.stopHorizontal();
                }
            }
        });
    }

    @Override
    protected void onFrame() {
        gameModel.update();
    }

    public GameView getGameView() {
        return gameView;
    }
}