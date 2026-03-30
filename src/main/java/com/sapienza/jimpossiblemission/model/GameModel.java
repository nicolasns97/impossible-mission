package com.sapienza.jimpossiblemission.model;

import com.sapienza.jimpossiblemission.model.physics.CollisionManager;
import com.sapienza.jimpossiblemission.model.physics.CollisionManager.CollisionResult;
import com.sapienza.jimpossiblemission.model.Room.RoomCode;
import com.sapienza.jimpossiblemission.model.physics.PhysicsEngine;
import com.sapienza.jimpossiblemission.model.state.ElevatorState;
import com.sapienza.jimpossiblemission.model.state.GameState;
import com.sapienza.jimpossiblemission.model.state.RoomState;
import com.sapienza.jimpossiblemission.model.state.TerminalState;

import java.awt.*;
import java.util.List;
import java.util.Observable;

public class GameModel extends Observable {

    // ==========================================
    // COSTANTI E VARIABILI
    // ==========================================
    private static final int SNOOZE_DURATION_FRAMES = 15 * 60; // 15 secondi a 60 fps
    private static final int COLLECTION_DURATION_FRAMES = (int) (1.5 * 60);
    private static final int TERMINAL_MSG_DURATION_FRAMES = 60;
    private static final int PLATFORM_TOLERANCE_Y = 5; // Margine di aggancio per le piattaforme

    // Manager e Modelli principali
    private final ElevatorAreaModel elevatorElevatorAreaModel;
    private final List<Room> rooms;
    private final GameWorld gameWorld;
    private final ElevatorModel elevator;
    private final AgentModel agent;
    private final CollisionManager collisionManager;
    private final PhysicsEngine physicsEngine;
    private final InteractionManager interactionManager;
    private final TerminalMenu terminalMenu;
    private final GameSession gameSession;

    // Macchina a Stati
    private GameState currentState;
    private final GameState elevatorState;
    private final GameState roomState;
    private final GameState terminalState;

    // Stato corrente di gioco
    private Room currentRoom;
    private Interactable currentInteractable;
    private Collectable collectable;
    private boolean collecting;
    private boolean snoozing;

    // Contatori
    private int collectingFrameCounter;
    private int showingPasswordRequiredFrameCounter;
    private int snoozeFrameCounter;

    // ==========================================
    // COSTRUTTORE
    // ==========================================
    public GameModel(ElevatorAreaModel elevatorElevatorAreaModel, List<Room> rooms, GameWorld gameWorld, ElevatorModel elevator, AgentModel agent, GameSession gameSession) {
        this.elevatorElevatorAreaModel = elevatorElevatorAreaModel;
        this.rooms = rooms;
        this.gameWorld = gameWorld;
        this.elevator = elevator;
        this.agent = agent;
        this.gameSession = gameSession;

        this.collisionManager = new CollisionManager();
        this.physicsEngine = new PhysicsEngine(collisionManager);
        this.interactionManager = new InteractionManager(collisionManager);
        this.terminalMenu = new TerminalMenu();

        this.elevatorState = new ElevatorState(this);
        this.roomState = new RoomState(this);
        this.terminalState = new TerminalState(this);
        this.currentState = elevatorState;

        this.currentRoom = null;
        this.currentInteractable = null;
        this.collecting = false;
        this.collectingFrameCounter = 0;
        this.snoozing = false;
    }

    // ==========================================
    // LOOP PRINCIPALE E LOGICHE DI STATO
    // ==========================================
    public void update() {
        currentState.update();
        setChanged();
        notifyObservers();
    }

    public void updateElevatorLogic() {
        // 1. Aree di collisione
        List<Rectangle> collisionObjects = elevatorElevatorAreaModel.getCollisionObjects(gameWorld.getWorldX(), gameWorld.getWorldY());
        collisionObjects.add(elevator.getBoundingBox());

        // 2. Fisica
        updateAgentPhysics(collisionObjects);

        // 3. Regole di interazione ascensore
        agent.setEnabled(!elevator.isMoving());
        elevator.setEnabled(!agent.isJumping() && elevator.isAgentOnElevator(agent));
        elevator.update();

        // 4. Transizioni
        handleRoomTransition();
    }

    public void updateRoomLogic() {
        // 1. Collisioni e piattaforme
        List<Rectangle> collisionObjects = currentRoom.getCollisionObjects();
        currentRoom.getPlatformGroups().forEach(PlatformGroup::update);

        // 2. Fisica
        updateAgentPhysics(collisionObjects);

        // 3. Timer e Nemici
        updateRoomTimers();
        updateEnemies();

        // 4. Interazioni con l'ambiente
        handleAgentOnPlatform();
        handleRoomTransition();
        handleInteractables();
    }

    // ==========================================
    // METODI PRIVATI DI SUPPORTO (HELPER)
    // ==========================================
    private void updateAgentPhysics(List<Rectangle> collisionObjects) {
        physicsEngine.applyPhysics(agent, collisionObjects);
    }

    private void updateEnemies() {
        List<Rectangle> roomSolids = currentRoom.getStaticCollisionObjects();
        for (Enemy e : currentRoom.getEnemies()) {
            e.update(roomSolids);
        }
    }

    private void handleInteractables() {
        currentInteractable = interactionManager.checkInteractions(agent, currentRoom);
        if (currentInteractable == null) {
            agent.stopInteracting();
        }
    }

    private void handleAgentOnPlatform() {
        Rectangle agentBox = agent.getBoundingBox();
        List<PlatformGroup> platformGroups = currentRoom.getPlatformGroups();
        boolean isAgentOnAnyPlatform = false;

        for (PlatformGroup platformGroup : platformGroups) {
            for (Platform platform : platformGroup.getPlatforms()) {
                Rectangle platformBox = platform.getBoundingBox();

                boolean onTop = agentBox.y + agentBox.height <= platformBox.y + PLATFORM_TOLERANCE_Y
                        && agentBox.y + agentBox.height >= platformBox.y - PLATFORM_TOLERANCE_Y
                        && agentBox.x + agentBox.width > platformBox.x
                        && agentBox.x < platformBox.x + platformBox.width;

                platform.setEnabled(onTop);

                if (onTop) {
                    isAgentOnAnyPlatform = true;
                    agent.setY(platformBox.y - agent.getHeight());
                    agent.setOnGround(true);

                    if (platform.isMoving()) {
                        int dy = platform.getMovingDirection() == Direction.UP ? -platform.getSpeed() : platform.getSpeed();
                        agent.setY(agent.getY() + dy);
                        agent.setEnabled(false);
                    } else {
                        agent.setEnabled(true);
                    }
                }
            }
        }

        if (!isAgentOnAnyPlatform && currentState == roomState) {
            agent.setEnabled(true);
        }
    }

    public void updateRoomTimers() {
        if (collecting) {
            collectingFrameCounter++;
            if (collectingFrameCounter == COLLECTION_DURATION_FRAMES) {
                collectingFrameCounter = 0;
                collecting = false;
            }
        }

        if (terminalMenu.isShowPasswordRequired()) {
            showingPasswordRequiredFrameCounter++;
            if (showingPasswordRequiredFrameCounter == TERMINAL_MSG_DURATION_FRAMES) {
                showingPasswordRequiredFrameCounter = 0;
                terminalMenu.setShowPasswordRequired(false);
            }
        }

        if (snoozing) {
            snoozeFrameCounter++;
            if (snoozeFrameCounter == SNOOZE_DURATION_FRAMES) {
                snoozing = false;
                snoozeFrameCounter = 0;
                currentRoom.getEnemies().forEach(Enemy::start);
            }
        }
    }

    // ==========================================
    // TRANSIZIONI DI STANZA E CARICAMENTI
    // ==========================================
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

    private void loadElevatorArea() {
        RoomTrigger roomTrigger = elevatorElevatorAreaModel.getRoomTrigger(currentRoom.getCode(), gameWorld.getWorldX(), gameWorld.getWorldY());
        int offset = 60;
        if (roomTrigger.getX() > elevatorElevatorAreaModel.getAgentSpawnPoint().getX()) {
            offset *= -1;
        }
        agent.setX(roomTrigger.getX() + offset);
        agent.setY(roomTrigger.getY() - 20);
        currentRoom = null;
        setState(elevatorState);
    }

    private void loadRoom(RoomCode roomCode) {
        currentRoom = rooms.stream().filter(r -> r.getCode().equals(roomCode)).findFirst().orElseThrow(IllegalStateException::new);
        agent.setX(currentRoom.getSpawnPosition().getX());
        agent.setY(currentRoom.getSpawnPosition().getY());
        for (Enemy e : currentRoom.getEnemies()) {
            e.reset();
        }
        setState(roomState);
    }

    // ==========================================
    // GESTIONE INPUT (DELEGATI ALLO STATO) E AZIONI
    // ==========================================
    public void up() { currentState.up(); }
    public void down() { currentState.down(); }
    public void space() { currentState.space(); }
    public void left() { currentState.left(); }
    public void right() { currentState.right(); }

    public void stopHorizontal() {
        agent.setMoving(false);
    }

    public void stopInteracting() {
        agent.stopInteracting();
        if (currentInteractable != null) {
            currentInteractable.stopInteracting();
        }
    }

    public void movePlatform(Direction direction) {
        currentRoom.getPlatformGroups().stream()
                .filter(group -> group.getPlatforms().stream().anyMatch(Platform::isEnabled))
                .findFirst()
                .ifPresent(group -> group.move(direction));
    }

    // ==========================================
    // GETTERS & SETTERS (STATI E MODELLI)
    // ==========================================
    public void setState(GameState newState) { this.currentState = newState; }
    public GameState getRoomState() { return roomState; }
    public GameState getElevatorState() { return elevatorState; }
    public GameState getTerminalState() { return terminalState; }

    public GameWorld getGameWorld() { return gameWorld; }
    public ElevatorModel getElevator() { return elevator; }
    public AgentModel getAgent() { return agent; }
    public CollisionManager getCollisionManager() { return collisionManager; }
    public ElevatorAreaModel getElevatorTileMapModel() { return elevatorElevatorAreaModel; }
    public List<Room> getRooms() { return rooms; }
    public Room getCurrentRoom() { return currentRoom; }
    public TerminalMenu getTerminalMenu() { return terminalMenu; }
    public GameSession getGameSession() { return gameSession; }

    // ==========================================
    // GETTERS & SETTERS (FLAG E VARIABILI DI GIOCO)
    // ==========================================
    public boolean isInElevatorArea() { return currentRoom == null; }
    public boolean isSnoozing() { return snoozing; }
    public void setSnoozing(boolean snoozing) { this.snoozing = snoozing; }
    public boolean isCollecting() { return collecting; }
    public void setCollecting(boolean collecting) { this.collecting = collecting; }
    public void setCollectingFrameCounter(int counter) { this.collectingFrameCounter = counter; }
    public Collectable getCollectable() { return this.collectable; }
    public void setCollectable(Collectable collectable) { this.collectable = collectable; }
    public Interactable getCurrentInteractable() { return currentInteractable; }
}