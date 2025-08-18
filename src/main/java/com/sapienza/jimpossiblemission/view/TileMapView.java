package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.model.TileMapModel;
import org.mapeditor.core.Tile;
import org.mapeditor.core.TileLayer;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileMapView {
    private final TileMapModel tileMapModel;

    public TileMapView(TileMapModel tileMapModel) {
        this.tileMapModel = tileMapModel;
    }

    public void draw(Graphics g, int cameraX, int cameraY, int screenWidth, int screenHeight) {
        TileLayer layer = (TileLayer) tileMapModel.getMap().getLayer(0);
        int tileWidth = tileMapModel.getMap().getTileWidth();
        int tileHeight = tileMapModel.getMap().getTileHeight();

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
                        BufferedImage image = tileMapModel.getTileImage(tile.getId());
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
