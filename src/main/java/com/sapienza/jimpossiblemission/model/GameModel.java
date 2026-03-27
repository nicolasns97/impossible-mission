package com.sapienza.jimpossiblemission.model;

import com.sapienza.jimpossiblemission.Direction;
import com.sapienza.jimpossiblemission.model.CollisionManager.CollisionResult;
import com.sapienza.jimpossiblemission.model.Room.RoomCode;

import java.awt.*;
import java.util.List;
import java.util.Observable;

public class GameModel extends Observable {
    private final ElevatorAreaModel elevatorElevatorAreaModel;
    private final List<Room> rooms;
    private final GameWorld gameWorld;
    private final ElevatorModel elevator;
    private final AgentModel agent;
    private final CollisionManager collisionManager;
    private Room currentRoom;
    private int frameCounter = 0;

    public GameModel(ElevatorAreaModel elevatorElevatorAreaModel, List<Room> rooms, GameWorld gameWorld, ElevatorModel elevator, AgentModel agent, CollisionManager collisionManager) {
        this.elevatorElevatorAreaModel = elevatorElevatorAreaModel;
        this.rooms = rooms;
        this.gameWorld = gameWorld;
        this.elevator = elevator;
        this.agent = agent;
        this.collisionManager = collisionManager;
        currentRoom = null;
    }

    public void update() {
        // 1. Aggiorna tutti i modelli (fisica, movimento, animazioni)
        updateEntities();

        // 2. Risolvi interazioni/collisioni (player ↔ elevator, player ↔ mondo, ecc.)
        handleInteractions();

        // 3. Aggiorna logiche di gioco (elevatore, missioni, punteggio, ecc.)
        updateGameLogic();

        // 4. Notifica la view
        setChanged();
        notifyObservers();
    }

    private void updateEntities() {
        // 0. Recupero le aree solide
        List<Rectangle> collisionObjects;
        if (isInElevatorArea()) {
            collisionObjects = elevatorElevatorAreaModel.getCollisionObjects(gameWorld.getWorldX(), gameWorld.getWorldY());
            collisionObjects.add(elevator.getBoundingBox());
        } else {
            collisionObjects = currentRoom.getCollisionObjects();
            currentRoom.getPlatformGroups().forEach(PlatformGroup::update);
        }

        // 1. Aggiorna salto/gravità
        agent.updateJump(); // buffer salto
        if (!agent.isOnGround()) {
            agent.applyGravity();
        }

        // 2. Movimento orizzontale: input del giocatore + impulso del salto
        int inputDx = 0;
        if (agent.isMoving()) {
            inputDx = agent.getDirection() == Direction.RIGHT ? agent.getSpeed() : -agent.getSpeed();
        }
        if (agent.isJumping()) {
            inputDx = (int) agent.getVelocityX();
        }

        int dx = inputDx;
        CollisionResult colX = collisionManager.checkCollision(collisionObjects, agent.getBoundingBox(), dx, 0);

        if (!colX.collided) {
            agent.setX(agent.getX() + dx);
        } else {
            // Se collide, azzera solo la componente del salto, input rimane
            agent.setVelocityX(0);
        }

        // 3. Movimento verticale
        int dy = (int) agent.getVelocityY();
        if (Math.abs(dy) < 1) dy = 0;

        CollisionResult colY = collisionManager.checkCollision(collisionObjects, agent.getBoundingBox(), 0, dy);

        if (!colY.collided) {
            agent.setY(agent.getY() + dy);
            agent.setOnGround(false);
        } else {
            if (dy > 0) { // caduta
                agent.setY(colY.collider.y - agent.getHeight());
                agent.land();
                agent.setVelocityX(0); // reset impulso salto quando atterra
            } else if (dy < 0) { // salto contro il soffitto
                agent.setY(agent.getY() + colY.intersection.height);
                agent.setVelocityY(0);
            }
        }

        if (!isInElevatorArea()) {
            frameCounter ++;
            if (frameCounter == 120) {
                //TODO change logic such that enemies turn back on collision end
                for (Enemy e : currentRoom.getEnemies()) {
                    PatrollingRobot p = (PatrollingRobot) e;
                    p.setDirection(p.getDirection() == Direction.LEFT ? Direction.RIGHT : Direction.LEFT);
                }
                frameCounter = 0;
            }
            currentRoom.getEnemies().forEach(Enemy::update);
        }
        // 4. Aggiorna l'ascensore
        elevator.update();
    }

    private void handleInteractions() {
        agent.setEnabled(!elevator.isMoving());
        elevator.setEnabled(!agent.isJumping() && elevator.isAgentOnElevator(agent));
        handleAgentOnPlatform();
        handleRoomTransition();
    }

    private void updateGameLogic() {

    }

    private void handleAgentOnPlatform() {
        if (isInElevatorArea()) return;
        Rectangle agentBox = agent.getBoundingBox();
        List<PlatformGroup> platformGroups = currentRoom.getPlatformGroups();

        for (PlatformGroup platformGroup : platformGroups) {
            for (Platform platform : platformGroup.getPlatforms()) {
                Rectangle platformBox = platform.getBoundingBox();

                boolean onTop = agentBox.y + agentBox.height <= platformBox.y + 5
                        && agentBox.y + agentBox.height >= platformBox.y - 5
                        && agentBox.x + agentBox.width > platformBox.x
                        && agentBox.x < platformBox.x + platformBox.width;

                platform.setEnabled(onTop);

                if (onTop) {
                    agent.setY(platformBox.y - agent.getHeight());
                    agent.setOnGround(true);

                    if (platform.isMoving()) {
                        int dy = platform.getMovingDirection() == Direction.UP ? -platform.getSpeed() : platform.getSpeed();
                        agent.setY(agent.getY() + dy);
                        agent.setEnabled(false);
                    }
                }
            }
        }
    }

    public void handleRoomTransition() {
        if (isInElevatorArea()) {
            RoomCode roomCode = null;
            for (RoomTrigger roomTrigger : elevatorElevatorAreaModel.getRoomTriggers(gameWorld.getWorldX(), gameWorld.getWorldY())) {
                CollisionResult collisionResult = collisionManager.checkPointCollision(roomTrigger.getX(), roomTrigger.getY(), agent.getBoundingBox());
                if (collisionResult.collided) {
                    roomCode = roomTrigger.getCode();
                    break;
                }
            }
            if (roomCode != null) {
                loadRoom(roomCode);
            }
        } else {
            Position exit = currentRoom.getExit();
            CollisionResult collisionResult = collisionManager.checkPointCollision(exit.getX(), exit.getY(), agent.getBoundingBox());
            if (collisionResult.collided) {
                loadElevatorArea();
            }
        }
    }

    public boolean isInElevatorArea() {
        return currentRoom == null;
    }

    private void loadElevatorArea() {
        RoomTrigger roomTrigger = elevatorElevatorAreaModel.getRoomTrigger(currentRoom.getCode(), gameWorld.getWorldX(), gameWorld.getWorldY());

        int offset = 60;
        if (roomTrigger.getX() > elevatorElevatorAreaModel.getAgentSpawnPoint().getX()) {
            offset *= -1;
        }
        agent.setX(roomTrigger.getX() + offset);
        agent.setY(roomTrigger.getY()-20);
        currentRoom = null;
    }

    private void loadRoom(RoomCode roomCode) {
        currentRoom = rooms.stream().filter(r -> r.getCode().equals(roomCode)).findFirst().orElseThrow(IllegalStateException::new);
        agent.setX(currentRoom.getSpawnPosition().getX() - 100);
        agent.setY(currentRoom.getSpawnPosition().getY());
        frameCounter = 0;
        for (Enemy e : currentRoom.getEnemies()) {
            e.reset();
        }
    }

    public void movePlatform(Direction direction) {
        currentRoom.getPlatformGroups().stream()
                .filter(group -> group.getPlatforms().stream().anyMatch(Platform::isEnabled))
                .findFirst()
                .ifPresent(group -> group.move(direction));
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public ElevatorModel getElevator() {
        return elevator;
    }

    public AgentModel getAgent() {
        return agent;
    }

    public CollisionManager getCollisionManager() {
        return collisionManager;
    }

    public ElevatorAreaModel getElevatorTileMapModel() {
        return elevatorElevatorAreaModel;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void up() {
        if (isInElevatorArea()) {
            elevator.startMoving(Direction.UP);
        } else {
            movePlatform(Direction.UP);
        }
    }

    public void down() {
        if (isInElevatorArea()) {
            elevator.startMoving(Direction.DOWN);
        } else {
            movePlatform(Direction.DOWN);
        }
    }
}
