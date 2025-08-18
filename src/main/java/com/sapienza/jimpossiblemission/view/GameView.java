package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.Debug;
import com.sapienza.jimpossiblemission.Direction;
import com.sapienza.jimpossiblemission.model.GameModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class GameView extends JPanel implements Observer {
    private final TileMapView tileMapView;
    private final ElevatorView elevatorView;
    private final AgentView agentView;
    private final int screenWidth = 1280;
    private final int screenHeight = 640;
    private int cameraX = 0;
    private int cameraY = 0;
    private int elevatorX;
    private int elevatorY;
    private int agentX;
    private int agentY;
    private boolean moving;
    private Direction agentDirection;
    private List<Rectangle> collisionObjects;

    public GameView(TileMapView tileMapView, ElevatorView elevatorView, AgentView agentView) {
        this.tileMapView = tileMapView;
        this.elevatorView = elevatorView;
        this.agentView = agentView;
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        tileMapView.draw(g2d, cameraX, cameraY, screenWidth, screenHeight);
        elevatorView.drawLift(g2d, elevatorX, elevatorY);
        agentView.draw(g2d, agentDirection, moving, agentX, agentY);
        elevatorView.drawDoors(g2d, elevatorX, elevatorY);
        elevatorView.drawRoof(g2d, cameraY);

        if (Debug.getInstance().isDebug()) {
            g.setColor(Color.BLUE);
            for (Rectangle solid : collisionObjects) {
                g.drawRect(solid.x, solid.y, solid.width, solid.height);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GameModel) {
            cameraX = ((GameModel) o).getGameWorld().getWorldX();
            cameraY = ((GameModel) o).getGameWorld().getWorldY();

            agentDirection = ((GameModel) o).getAgent().getDirection();
            moving = ((GameModel) o).getAgent().isMoving();
            elevatorX = ((GameModel) o).getElevator().getX();
            elevatorY = ((GameModel) o).getElevator().getY();

            agentX = ((GameModel) o).getAgent().getX();
            agentY = ((GameModel) o).getAgent().getY();

            collisionObjects = ((GameModel) o).getTileMap().getCollisionObjects(cameraX, cameraY);
        }
    }

}
