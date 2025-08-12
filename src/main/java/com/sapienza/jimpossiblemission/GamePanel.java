package com.sapienza.jimpossiblemission;

import org.mapeditor.core.Map;
import org.mapeditor.core.Tile;
import org.mapeditor.core.TileLayer;
import org.mapeditor.io.TMXMapReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class GamePanel extends JPanel implements Runnable {
    final int FPS = 60;
    final int originalTileSize = 16;
    final int scale = 2;

    final int tileSize = originalTileSize * scale;
    final int maxScreenCol = 40;
    final int maxScreenRow = 20;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;
    Thread gameThread;
    public Map map = null;
    BufferedImage tilesetImage = null;
    java.util.Map<Integer, BufferedImage> tileImages = new HashMap<>();
    LiftKeyHandler liftKeyHandler = new LiftKeyHandler();
    PlayerKeyHandler playerKeyHandler = new PlayerKeyHandler();
    Lift lift;
    Player player;
    int cameraX = 0;
    int cameraY = 0;

    public GamePanel() throws IOException {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(liftKeyHandler);
        this.addKeyListener(playerKeyHandler);
        this.setFocusable(true);
        loadTileMap();
        lift = new Lift(this, liftKeyHandler);
        player = new Player(this, playerKeyHandler);
    }

    private void loadTileMap() {
        try {
            tilesetImage = ImageIO.read(Objects.requireNonNull(Main.class.getResource("/map/tileset_test.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TMXMapReader reader = null;
        try {
            reader = new TMXMapReader();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        try {
            map = reader.readMap("src/main/resources/map/test.tmx");
            System.out.println("Mappa caricata: ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        int tileWidth = map.getTileWidth();
        int tilesetColumns = tilesetImage.getWidth() / tileWidth;
        int tileHeight = map.getTileHeight();
        int tilesetRows = tilesetImage.getHeight() / tileHeight;
        int tileCount = tilesetColumns * tilesetRows;
        for (int gid = 0; gid < tileCount; gid++) {
            int sx = (gid % tilesetColumns) * tileWidth;
            int sy = (gid / tilesetColumns) * tileHeight;

            BufferedImage tileImage = tilesetImage.getSubimage(sx, sy, tileWidth, tileHeight);
            tileImages.put(gid, tileImage);
        }

    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();

    }

    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {
            currentTime = System.nanoTime();

            delta +=  (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        lift.update();
        player.update(!lift.moving);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawTiles(g2d);
        lift.drawLift(g2d);
        player.draw(g2d);
        lift.drawDoors(g2d);
    }

    public void drawTiles(Graphics2D g) {
        int tileWidth = map.getTileWidth();
        int tileHeight = map.getTileHeight();

        TileLayer layer = (TileLayer) map.getLayer(0);

        // Calcola il tile di partenza
        int startCol = cameraX / tileWidth;
        int startRow = cameraY / tileHeight;

        // Offset all'interno della tile (per effetto di scorrimento fluido)
        int offsetX = cameraX % tileWidth;
        int offsetY = cameraY % tileHeight;

        for (int screenRow = 0; screenRow < maxScreenRow + 1; screenRow++) {
            for (int screenCol = 0; screenCol < maxScreenCol + 1; screenCol++) {

                int mapCol = startCol + screenCol;
                int mapRow = startRow + screenRow;

                if (mapCol >= 0 && mapRow >= 0 &&
                        mapCol < layer.getWidth() && mapRow < layer.getHeight()) {

                    Tile tile = layer.getTileAt(mapCol, mapRow);
                    if (tile != null) {
                        int gid = tile.getId();
                        BufferedImage tileImage = tileImages.get(gid);
                        if (tileImage != null) {
                            int drawX = screenCol * tileWidth - offsetX;
                            int drawY = screenRow * tileHeight - offsetY;
                            g.drawImage(tileImage, drawX, drawY, null);
                        }
                    }
                }
            }
        }
    }
}
