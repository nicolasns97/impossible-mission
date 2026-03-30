package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.Debug;
import com.sapienza.jimpossiblemission.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class GameView extends JPanel implements Observer {
    private static final int SCREEN_WIDTH = 1280;
    private static final int SCREEN_HEIGHT = 640;

    private final GameModel gameModel;
    private final ElevatorAreaView elevatorAreaView;
    private final ElevatorView elevatorView;
    private final AgentView agentView;
    private final RoomView roomView;
    private final CollectableView collectableView;
    private final TerminalMenuView terminalMenuView;
    private final HUDView hudView;
    private Room room;

    public GameView(GameModel gameModel, ElevatorAreaView elevatorAreaView, ElevatorView elevatorView, AgentView agentView, RoomView roomView, CollectableView collectableView, TerminalMenuView terminalMenuView, HUDView hudView) {
        this.gameModel = gameModel;
        this.elevatorAreaView = elevatorAreaView;
        this.elevatorView = elevatorView;
        this.agentView = agentView;
        this.roomView = roomView;
        this.collectableView = collectableView;
        this.terminalMenuView = terminalMenuView;
        this.hudView = hudView;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (gameModel.getTerminalMenu().isActive()) {
            drawTerminalMenu(g, g2d);
        } else if (gameModel.isInElevatorArea()) {
            drawElevatorArea(g, g2d);
        } else {
            if (room == null || room != gameModel.getCurrentRoom()) {
                room = gameModel.getCurrentRoom();
                roomView.setRoom(gameModel.getCurrentRoom());
            }
            drawRoom(g, g2d);
        }
        hudView.draw(g2d);
    }

    private void drawTerminalMenu(Graphics g, Graphics2D g2d) {
        terminalMenuView.draw(g2d);
    }

    private void drawRoom(Graphics g, Graphics2D g2d) {
        AgentModel agent = gameModel.getAgent();
        roomView.draw(g2d, agent.getX(), agent.getY());
        agentView.draw(g2d);

        if (gameModel.isCollecting()) {
            Collectable collectable = gameModel.getCollectable();
            collectableView.draw(g2d, collectable, agent.getX(), agent.getY());
        }

        if (Debug.getInstance().isDebug()) {
            g.setColor(Color.YELLOW);
            for (Rectangle solid : gameModel.getCurrentRoom().getCollisionObjects()) {
                g.drawRect(solid.x, solid.y, solid.width, solid.height);
            }
        }
    }

    private void drawElevatorArea(Graphics g, Graphics2D g2d) {
        int cameraX = gameModel.getGameWorld().getWorldX();
        int cameraY = gameModel.getGameWorld().getWorldY();

        elevatorAreaView.draw(g2d, cameraX, cameraY, SCREEN_WIDTH, SCREEN_HEIGHT);
        elevatorView.drawLift(g2d);
        agentView.draw(g2d);
        elevatorView.drawDoors(g2d);
        elevatorView.drawRoof(g2d, cameraY);

        if (Debug.getInstance().isDebug()) {
            g.setColor(Color.YELLOW);
            for (Rectangle solid : gameModel.getElevatorTileMapModel().getCollisionObjects(cameraX, cameraY)) {
                g.drawRect(solid.x, solid.y, solid.width, solid.height);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

}
