package com.sapienza.jimpossiblemission.model;

import com.sapienza.jimpossiblemission.model.CollisionManager.CollisionResult;
import com.sapienza.jimpossiblemission.model.Room.RoomCode;
import com.sapienza.jimpossiblemission.model.state.ElevatorState;
import com.sapienza.jimpossiblemission.model.state.GameState;
import com.sapienza.jimpossiblemission.model.state.RoomState;
import com.sapienza.jimpossiblemission.model.state.TerminalState;

import java.awt.*;
import java.util.List;
import java.util.Observable;
import java.util.Optional;

public class GameModel extends Observable {
    private final ElevatorAreaModel elevatorElevatorAreaModel;
    private final List<Room> rooms;
    private final GameWorld gameWorld;
    private final ElevatorModel elevator;
    private final AgentModel agent;
    private final CollisionManager collisionManager;
    private final TerminalMenu terminalMenu;
    private final GameSession gameSession;

    private GameState currentState;
    private final GameState elevatorState;
    private final GameState roomState;
    private final GameState terminalState;

    private Room currentRoom;
    private Interactable currentInteractable;
    private boolean collecting;
    private int collectingFrameCounter;
    private Collectable collectable;
    private int showingPasswordRequiredFrameCounter;
    private boolean snoozing;
    private int snoozeFrameCounter;

    public GameModel(ElevatorAreaModel elevatorElevatorAreaModel, List<Room> rooms, GameWorld gameWorld, ElevatorModel elevator, AgentModel agent, CollisionManager collisionManager, TerminalMenu terminalMenu, GameSession gameSession) {
        this.elevatorElevatorAreaModel = elevatorElevatorAreaModel;
        this.rooms = rooms;
        this.gameWorld = gameWorld;
        this.elevator = elevator;
        this.agent = agent;
        this.collisionManager = collisionManager;
        this.terminalMenu = terminalMenu;
        this.gameSession = gameSession;

        // STATE PATTERN
        this.elevatorState = new ElevatorState(this);
        this.roomState = new RoomState(this);
        this.terminalState = new TerminalState(this);
        this.currentState = terminalState;

        currentRoom = null;
        currentInteractable = null;
        collecting = false;
        collectingFrameCounter = 0;
        snoozing = false;
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
            if (dy > 0) {
                agent.setY(colY.collider.y - agent.getHeight());
                agent.land();
                agent.setVelocityX(0);
            } else if (dy < 0) {
                agent.setY(agent.getY() + colY.intersection.height);
                agent.setVelocityY(0);
            }
        }

        if (!isInElevatorArea()) {
            if (collecting) {
                collectingFrameCounter++;
                if (collectingFrameCounter == 90) {
                    collectingFrameCounter = 0;
                    collecting = false;
                }
            }

            if (terminalMenu.isShowPasswordRequired()) {
                showingPasswordRequiredFrameCounter++;
                if (showingPasswordRequiredFrameCounter == 60) {
                    showingPasswordRequiredFrameCounter = 0;
                    terminalMenu.setShowPasswordRequired(false);
                }
            }

            if (snoozing) {
                snoozeFrameCounter++;
                if (snoozeFrameCounter == 15 * 60) {
                    snoozing = false;
                    snoozeFrameCounter = 0;
                    currentRoom.getEnemies().forEach(Enemy::start);
                }
            }

            List<Rectangle> roomSolids = currentRoom.getStaticCollisionObjects();

            for (Enemy e : currentRoom.getEnemies()) {
                if (e instanceof PatrollingRobot) {
                    PatrollingRobot p = (PatrollingRobot) e;

                    // 2. Posizioniamo il sensore "salva-vita"
                    // Lo mettiamo leggermente avanti rispetto ai piedi del robot
                    int sensorX = (p.getDirection() == Direction.RIGHT)
                            ? p.getX() + p.getWidth() + 5
                            : p.getX() - 15;

                    // Creiamo un sensore verticale più lungo (10px) per intercettare la piattaforma
                    // anche se il robot non è perfettamente allineato al pixel
                    Rectangle sensorDown = new Rectangle(sensorX, p.getY() + p.getHeight(), 1, 10);

                    // 3. Verifichiamo se c'è pavimento davanti
                    boolean floorAhead = false;
                    for (Rectangle solid : roomSolids) {
                        if (solid.intersects(sensorDown)) {
                            floorAhead = true;
                            break;
                        }
                    }

                    // 4. LOGICA DI MOVIMENTO
                    if (!floorAhead) {
                        // Se non c'è pavimento, girati e basta (NON muoverti in questo frame)
                        p.invertDirection();
                    } else {
                        // Se la strada è sicura, procedi con l'update del movimento
                        p.update();
                    }
                } else {
                    // Se non è un PatrollingRobot (es. altri nemici), aggiorna normalmente
                    e.update();
                }
            }
        }
        // 4. Aggiorna l'ascensore
        elevator.update();
    }

    private void handleInteractions() {
        agent.setEnabled(!elevator.isMoving());
        elevator.setEnabled(!agent.isJumping() && elevator.isAgentOnElevator(agent));
        handleAgentOnPlatform();
        handleRoomTransition();
        handleInteractables();
    }

    private void handleInteractables() {
        if (isInElevatorArea()) return;

        for (Container container : currentRoom.getContainers()) {
            if (container.getSearchCompletion() >= 100) continue;
            CollisionResult collisionResult = collisionManager.checkCollision(container.getBoundingBox(), agent.getBoundingBox());
            if (collisionResult.collided) {
                currentInteractable = container;
                return;
            }
        }
        if (currentRoom.getTerminal().isPresent()) {
            Terminal terminal = currentRoom.getTerminal().get();
            CollisionResult collisionResult = collisionManager.checkCollision(terminal.getBoundingBox(), agent.getBoundingBox());
            if (collisionResult.collided) {
                currentInteractable = terminal;
                return;
            }
        }
        currentInteractable = null;
        agent.stopInteracting();
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
        if (terminalMenu.isActive()) {
            terminalMenu.up();
        } else if (isInElevatorArea()) {
            elevator.startMoving(Direction.UP);
        } else {
            movePlatform(Direction.UP);
            if (currentInteractable != null) {
                agent.startInteracting();
                currentInteractable.interact();
                if (currentInteractable instanceof Container) {
                    Container container = (Container) currentInteractable;
                    Optional<Collectable> collectable = container.collect();
                    if (collectable.isPresent()) {
                        collecting = true;
                        this.collectable = collectable.get();
                        gameSession.getInventory().add(collectable.get());
                    } else {
                        collecting = false;
                        collectingFrameCounter = 0;
                    }
                } else if (currentInteractable instanceof Terminal) {
                    currentRoom.getEnemies().forEach(Enemy::stop);
                    terminalMenu.setActive(true);
                }
            }
        }
    }

    public void down() {
        if (terminalMenu.isActive()) {
            terminalMenu.down();
        } else if (isInElevatorArea()) {
            elevator.startMoving(Direction.DOWN);
        } else {
            movePlatform(Direction.DOWN);
        }
    }

    public void space() {
        if (terminalMenu.isActive()) {
            int selectResult = terminalMenu.select(gameSession.getInventory());

            if (selectResult == 0) {
                for (PlatformGroup platformGroup : currentRoom.getPlatformGroups()) {
                    for (Platform platform : platformGroup.getPlatforms()) {
                        platform.resetPosition();
                    }
                }
                currentRoom.getEnemies().forEach(Enemy::start);
            } else if (selectResult == 1) {
                snoozing = true;
            } else if (selectResult == 2 && !snoozing) {
                currentRoom.getEnemies().forEach(Enemy::start);
            }

        } else if (agent.isEnabled())
            agent.pressJump();
    }

    public void stopInteracting() {
        agent.stopInteracting();
        if (currentInteractable != null) {
            currentInteractable.stopInteracting();
        }
    }

    public boolean isCollecting() {
        return collecting;
    }

    public Collectable getCollectable() {
        return this.collectable;
    }

    public TerminalMenu getTerminalMenu() {
        return terminalMenu;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public boolean isSnoozing() {
        return snoozing;
    }

    public void setSnoozing(boolean snoozing) {
        this.snoozing = snoozing;
    }

    public void setState(GameState newState) {
        this.currentState = newState;
    }
    public GameState getRoomState() { return roomState; }
    public GameState getElevatorState() { return elevatorState; }
    public GameState getTerminalState() { return terminalState; }
}
