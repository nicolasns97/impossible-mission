package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.Debug;
import com.sapienza.jimpossiblemission.Direction;
import com.sapienza.jimpossiblemission.model.*;
import com.sapienza.jimpossiblemission.model.Room.RoomCode;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

import static java.awt.Color.*;

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
    private boolean agentJumping;
    private List<Rectangle> collisionObjects;
    private List<Position> platforms;
    private Room currentRoom;
    private RoomView roomView;

    public GameView(TileMapView tileMapView, ElevatorView elevatorView, AgentView agentView) {
        this.tileMapView = tileMapView;
        this.elevatorView = elevatorView;
        this.agentView = agentView;
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        roomView = new RoomView();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        /**
        tileMapView.draw(g2d, cameraX, cameraY, screenWidth, screenHeight);
        elevatorView.drawLift(g2d, elevatorX, elevatorY);
        agentView.draw(g2d, agentDirection, moving, agentX, agentY, agentJumping);
        elevatorView.drawDoors(g2d, elevatorX, elevatorY);
        elevatorView.drawRoof(g2d, cameraY);

        if (Debug.getInstance().isDebug()) {
            g.setColor(Color.BLUE);
            for (Rectangle solid : collisionObjects) {
                g.drawRect(solid.x, solid.y, solid.width, solid.height);
            }
        } */
        roomView.draw(g2d, platforms);
        agentView.draw(g2d, agentDirection, moving, agentX, agentY, agentJumping);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GameModel) {
            GameModel gameModel = (GameModel) o;
            cameraX = gameModel.getGameWorld().getWorldX();
            cameraY = gameModel.getGameWorld().getWorldY();

            agentDirection = gameModel.getAgent().getDirection();
            moving = gameModel.getAgent().isMoving();
            elevatorX = gameModel.getElevator().getX();
            elevatorY = gameModel.getElevator().getY();

            agentX = gameModel.getAgent().getX();
            agentY = gameModel.getAgent().getY();
            agentJumping = gameModel.getAgent().isJumping();

            collisionObjects = gameModel.getTileMap().getCollisionObjects(cameraX, cameraY);

            platforms = gameModel.getRooms().get(0).getPlatformGroups().stream()
                    .flatMap(platformGroup -> platformGroup.getPlatforms().stream())
                    .map(Entity::getPosition)
                    .collect(Collectors.toList());

            Room gameModelCurrentRoom = gameModel.getCurrentRoom();
            if (gameModelCurrentRoom != null && !gameModelCurrentRoom.equals(currentRoom)) {
                currentRoom = gameModelCurrentRoom;
                roomView.setRoom(currentRoom);
            }
        }
    }

}
