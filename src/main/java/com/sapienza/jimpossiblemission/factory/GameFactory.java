package com.sapienza.jimpossiblemission.factory;

import com.sapienza.jimpossiblemission.model.Direction;
import com.sapienza.jimpossiblemission.Main;
import com.sapienza.jimpossiblemission.controller.GameController;
import com.sapienza.jimpossiblemission.model.*;
import com.sapienza.jimpossiblemission.model.Room.RoomCode;
import com.sapienza.jimpossiblemission.model.RoomMetadataReader.RoomMetadata;
import com.sapienza.jimpossiblemission.view.*;
import org.mapeditor.io.TMXMapReader;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

/**
 * Factory per creare e inizializzare i componenti principali del gioco.
 * <p>
 * Questa classe funge da punto centralizzato per il wiring di tutti i componenti
 * MVC dell'applicazione: modelli, viste e controller. Permette al {@link Main}
 * di rimanere snello e concentrato solo sull'avvio dell'applicazione.
 * </p>
 *
 * @author Nicola Sansonetti
 */
public class GameFactory {
    private static final int AGENT_BOUNDING_BOX_WIDTH = 80;
    private static final int AGENT_BOUNDING_BOX_HEIGHT = 100;
    private static final int AGENT_SPEED = 5;

    private static final int ELEVATOR_SPEED = 18;
    private static final int ELEVATOR_SPAWN_X = 544;
    private static final int ELEVATOR_SPAWN_Y = 0;
    private static final int ELEVATOR_BOUNDING_BOX_WIDTH = 192;
    private static final int ELEVATOR_BOUNDING_BOX_HEIGHT = 352;

    private static final String ELEVATOR_TMX_PATH = "src/main/resources/map/elevator.tmx";
    private static final String TILESET_PATH = "/map/elevator_tileset.png";
    private static final String ELEVATOR_BASE_SPRITE_PATH = "/elevator/base_sprite.png";
    private static final String ELEVATOR_DOOR_SPRITE_PATH = "/elevator/doors_sprite.png";
    public static final String ROOF_SPRITE_PATH = "/elevator/roof_sprite.png";
    private static final String AGENT_SPRITE_DIRECTORY = "/agent";

    public static GameController createGame() {
        ElevatorAreaModel elevatorElevatorAreaModel = new ElevatorAreaModel(ELEVATOR_TMX_PATH);
        GameModel gameModel = createGameModel(elevatorElevatorAreaModel);

        ElevatorAreaView elevatorAreaView = new ElevatorAreaView(elevatorElevatorAreaModel, TILESET_PATH);
        ElevatorView elevatorView = new ElevatorView(gameModel.getElevator(), ELEVATOR_BASE_SPRITE_PATH, ELEVATOR_DOOR_SPRITE_PATH, ROOF_SPRITE_PATH);
        AgentView agentView = new AgentView(gameModel.getAgent(), AGENT_SPRITE_DIRECTORY);
        RoomView roomView = createRoomView(gameModel.getRooms());
        TerminalMenuView terminalMenuView = new TerminalMenuView(gameModel.getTerminalMenu());
        GameView gameView = new GameView(gameModel,
                elevatorAreaView,
                elevatorView,
                agentView,
                roomView,
                new CollectableView(),
                terminalMenuView,
                new HUDView(gameModel.getGameSession())
        );

        return new GameController(gameModel, gameView);
    }

    private static RoomView createRoomView(List<Room> rooms) {
        Map<RoomCode, BufferedImage> roomBackgroundMap = new HashMap<>();
        Map<RoomCode, BufferedImage> platformSpriteMap = new HashMap<>();
        try {
            for (Room room : rooms) {
                RoomCode roomCode = room.getCode();
                roomBackgroundMap.put(roomCode, ImageIO.read(Objects.requireNonNull(GameFactory.class.getResourceAsStream(String.format("/room/%s.png", roomCode.getPath())))));
                platformSpriteMap.put(roomCode, ImageIO.read(Objects.requireNonNull(GameFactory.class.getResourceAsStream(String.format("/platform/%s.png", roomCode.getPath())))));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new RoomView(roomBackgroundMap, platformSpriteMap);
    }

    private static GameModel createGameModel(ElevatorAreaModel elevatorAreaModel) {
        GameWorld gameWorld = new GameWorld(0, 0);
        Point spawnPoint = elevatorAreaModel.getAgentSpawnPoint();
        ElevatorModel elevator = new ElevatorModel(gameWorld,
                elevatorAreaModel.getElevatorStops(),
                ELEVATOR_SPEED,
                ELEVATOR_SPAWN_X,
                ELEVATOR_SPAWN_Y,
                ELEVATOR_BOUNDING_BOX_WIDTH,
                ELEVATOR_BOUNDING_BOX_HEIGHT);

        List<Room> rooms = new ArrayList<>();
        rooms.add(createRoom(RoomCode.ECHO_CHAMBER));
        rooms.add(createRoom(RoomCode.SHADOW_VAULT));
        rooms.add(createRoom(RoomCode.SILICON_LAB));
        rooms.add(createRoom(RoomCode.SENTRY_HALL));
        rooms.add(createRoom(RoomCode.DATA_GRID_ROOM));
        rooms.add(createRoom(RoomCode.CONTROL_NODE));
        rooms.add(createRoom(RoomCode.MECH_CORRIDOR));
        rooms.add(createRoom(RoomCode.CORE_TERMINAL));

        AgentModel agent = new AgentModel(spawnPoint.x,
                spawnPoint.y,
                AGENT_BOUNDING_BOX_WIDTH,
                AGENT_BOUNDING_BOX_HEIGHT,
                AGENT_SPEED,
                Direction.LEFT);
        CollisionManager collisionManager = new CollisionManager();
        Inventory inventory = new Inventory();
        inventory.add(new Code(Code.CodeType.SNOOZE));
        inventory.add(new Code(Code.CodeType.PLATFORM_RESET));
        List<Life> lives = new ArrayList<>();
        lives.add(new Life());
        lives.add(new Life());
        lives.add(new Life());
        lives.add(new Life());
        lives.add(new Life());
        GameTimer gameTimer = new GameTimer(10,0);
        gameTimer.start();
        GameSession gameSession = new GameSession(
                inventory,
                agent,
                rooms,
                lives,
                gameTimer
        );
        return new GameModel(elevatorAreaModel, rooms, gameWorld, elevator, agent, collisionManager, new TerminalMenu(), gameSession);
    }

    public static Room createRoom(RoomCode code) {
        Room room;
        try {
            TMXMapReader reader = new TMXMapReader();
            RoomMetadataReader roomMetadataReader = new RoomMetadataReader(reader);
            RoomMetadata roomMetadata = roomMetadataReader.readRoomMetadata(getRoomPath(code));

            room = new Room(code,
                    roomMetadata.getSpawnPosition(),
                    roomMetadata.getExit(),
                    roomMetadata.getCollisionObjects(),
                    roomMetadata.getEnemies(),
                    roomMetadata.getContainers(),
                    roomMetadata.getTerminal(),
                    PlatformFactory.createPlatformGroups(roomMetadata.getPlatforms(), roomMetadata.getPlatformsStops()));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return room;
    }


    private static String getRoomPath(RoomCode code) {
        Path filePath = Paths.get("src", "main", "resources", "room", code.getPath() + ".tmx");
        return filePath.toString();
    }
}
