package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.model.ElevatorAreaModel;
import org.mapeditor.core.Tile;
import org.mapeditor.core.TileLayer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ElevatorAreaView {
    private final ElevatorAreaModel elevatorAreaModel;
    private final Map<Integer, BufferedImage> tileImages = new HashMap<>();


    public ElevatorAreaView(ElevatorAreaModel model, String tilesetPath) {
        this.elevatorAreaModel = model;

        BufferedImage tileset;
        try {
            tileset = ImageIO.read(Objects.requireNonNull(getClass().getResource(tilesetPath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int tileWidth = model.getMap().getTileWidth();
        int tileHeight = model.getMap().getTileHeight();
        int cols = tileset.getWidth() / tileWidth;
        int rows = tileset.getHeight() / tileHeight;

        for (int gid = 0; gid < cols * rows; gid++) {
            int sx = (gid % cols) * tileWidth;
            int sy = (gid / cols) * tileHeight;
            tileImages.put(gid, tileset.getSubimage(sx, sy, tileWidth, tileHeight));
        }
    }

    public void draw(Graphics g, int cameraX, int cameraY, int screenWidth, int screenHeight) {
        TileLayer layer = (TileLayer) elevatorAreaModel.getMap().getLayer(0);
        int tileWidth = elevatorAreaModel.getMap().getTileWidth();
        int tileHeight = elevatorAreaModel.getMap().getTileHeight();

        // Calcola tile di partenza in base alla camera
        int startCol = cameraX / tileWidth;
        int startRow = cameraY / tileHeight;

        // Offset dentro la tile per scorrimento fluido
        int offsetX = cameraX % tileWidth;
        int offsetY = cameraY % tileHeight;

        // Quante tile disegnare sullo schermo
        int colsOnScreen = screenWidth / tileWidth + 2; // +2 per coprire parziale
        int rowsOnScreen = screenHeight / tileHeight + 2;

        for (int row = 0; row < rowsOnScreen; row++) {
            for (int col = 0; col < colsOnScreen; col++) {
                int mapCol = startCol + col;
                int mapRow = startRow + row;

                if (mapCol >= 0 && mapRow >= 0 && mapCol < layer.getWidth() && mapRow < layer.getHeight()) {
                    Tile tile = layer.getTileAt(mapCol, mapRow);
                    if (tile != null) {
                        BufferedImage image = tileImages.get(tile.getId());
                        if (image != null) {
                            int drawX = col * tileWidth - offsetX;
                            int drawY = row * tileHeight - offsetY;
                            g.drawImage(image, drawX, drawY, null);
                        }
                    }
                }
            }
        }
    }

}
