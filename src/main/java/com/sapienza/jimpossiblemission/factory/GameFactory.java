package com.sapienza.jimpossiblemission.factory;

import com.sapienza.jimpossiblemission.Direction;
import com.sapienza.jimpossiblemission.Main;
import com.sapienza.jimpossiblemission.controller.GameController;
import com.sapienza.jimpossiblemission.model.*;
import com.sapienza.jimpossiblemission.model.Room.RoomCode;
import com.sapienza.jimpossiblemission.model.RoomMetadataReader.RoomMetadata;
import com.sapienza.jimpossiblemission.view.AgentView;
import com.sapienza.jimpossiblemission.view.ElevatorView;
import com.sapienza.jimpossiblemission.view.GameView;
import com.sapienza.jimpossiblemission.view.TileMapView;
import org.mapeditor.io.TMXMapReader;

import javax.xml.bind.JAXBException;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

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

    private static final String TMX_PATH = "src/main/resources/map/test.tmx";
    private static final String TILESET_PATH = "/map/tileset_test.png";
    private static final String ELEVATOR_BASE_SPRITE_PATH = "/elevator/base_sprite.png";
    private static final String ELEVATOR_DOOR_SPRITE_PATH = "/elevator/doors_sprite.png";
    public static final String ROOF_SPRITE_PATH = "/elevator/roof_sprite.png";
    private static final String AGENT_SPRITE_DIRECTORY = "/agent";

    public static GameController createGame() {
        TileMapModel tileMapModel = new TileMapModel(TMX_PATH, TILESET_PATH);
        GameModel gameModel = createGameModel(tileMapModel);

        TileMapView tileMapView = new TileMapView(tileMapModel);
        ElevatorView elevatorView = new ElevatorView(ELEVATOR_BASE_SPRITE_PATH, ELEVATOR_DOOR_SPRITE_PATH, ROOF_SPRITE_PATH);
        AgentView agentView = new AgentView(AGENT_SPRITE_DIRECTORY);
        GameView gameView = new GameView(tileMapView,
                elevatorView,
                agentView);

        return new GameController(gameModel, gameView);
    }

    private static GameModel createGameModel(TileMapModel tileMapModel) {
        GameWorld gameWorld = new GameWorld(0, 0);
        Point spawnPoint = tileMapModel.getAgentSpawnPoint();
        ElevatorModel elevator = new ElevatorModel(gameWorld,
                tileMapModel.getElevatorStops(),
                ELEVATOR_SPEED,
                ELEVATOR_SPAWN_X,
                ELEVATOR_SPAWN_Y,
                ELEVATOR_BOUNDING_BOX_WIDTH,
                ELEVATOR_BOUNDING_BOX_HEIGHT);
        List<Room> rooms = new ArrayList<>();
        rooms.add(createRoom(RoomCode.ECHO_CHAMBER));
        AgentModel agent = new AgentModel(rooms.get(0).getSpawnPosition().getX()-100,//300,
                rooms.get(0).getSpawnPosition().getY()-AGENT_BOUNDING_BOX_HEIGHT,//spawnPoint.y,
                AGENT_BOUNDING_BOX_WIDTH,
                AGENT_BOUNDING_BOX_HEIGHT,
                AGENT_SPEED,
                Direction.LEFT);

        CollisionManager collisionManager = new CollisionManager();


        return new GameModel(tileMapModel, rooms, gameWorld, elevator, agent, collisionManager);
    }

    public static Room createRoom(RoomCode code) {
        Room room;
        try {
            TMXMapReader reader = new TMXMapReader();
            RoomMetadataReader roomMetadataReader = new RoomMetadataReader(reader);
            RoomMetadata roomMetadata = roomMetadataReader.readRoomMetadata(getRoomPath(code));

            room = new Room(code,
                    roomMetadata.getSpawnPosition(),
                    roomMetadata.getCollisionObjects(),
                    EnemyFactory.createEnemies(roomMetadata.getEnemySpawnPositions()),
                    null,
                    null,
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
