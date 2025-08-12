package com.sapienza.jimpossiblemission;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Player {
    PlayerKeyHandler playerKeyHandler;
    GamePanel gamePanel;
    BufferedImage sprite_idle_rx;
    BufferedImage sprite_idle_lx;
    BufferedImage[] spritesRunRight = new BufferedImage[15];
    BufferedImage[] spritesRunLeft = new BufferedImage[15];
    int x;
    int y;
    int speed;
    String direction;

    int frameIndex = 0;        // quale frame stiamo disegnando
    int frameCounter = 0;      // per il tempo dell'animazione
    int frameDelay = 3;        // numero di cicli di game loop prima di passare al prossimo frame

    public Player(GamePanel gamePanel, PlayerKeyHandler playerKeyHandler) {
        this.gamePanel = gamePanel;
        this.playerKeyHandler = playerKeyHandler;
        this.x = 618;
        this.y = 217-(96*2);
        this.speed = 6;
        this.direction = "left";
        loadSprite();
    }

    public void loadSprite() {
        try {
            sprite_idle_rx = ImageIO.read(Objects.requireNonNull(getClass().getResource("/player/idle/player_idle_rx.png")));
            sprite_idle_lx = ImageIO.read(Objects.requireNonNull(getClass().getResource("/player/idle/player_idle_lx.png")));
            for (int i = 0; i < 15; i++) {
                BufferedImage raw = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/run/player_run_" + i + "_rx.png")));
                spritesRunRight[i] = scaleSprite(normalizeSprite(raw, 48, 96), 3);
                raw  = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/run/player_run_" + i + "_lx.png")));
                spritesRunLeft[i] = scaleSprite(normalizeSprite(raw, 48, 96), 3);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void draw(Graphics2D g) {
        BufferedImage image = null;
        switch (direction) {
            case "left"  -> image = spritesRunLeft[frameIndex];
            case "right" -> image = spritesRunRight[frameIndex];
        }
        g.drawImage(image, x, y, image.getWidth(), image.getHeight(), null);
        g.setColor(Color.RED);
       // g.drawRect(x, y, image.getWidth(), image.getHeight());
    }

    public void update(boolean movementEnabled) {
        if (!movementEnabled) {
            frameIndex = 0;
            return;
        }
        boolean moving = false;

        if (playerKeyHandler.left) {
            direction = "left";
            this.x -= speed;
            moving = true;
        } else if (playerKeyHandler.right) {
            direction = "right";
            this.x += speed;
            moving = true;
        }

        // Animazione
        if (moving) {
            frameCounter++;
            if (frameCounter >= frameDelay) {
                frameIndex = (frameIndex + 1) % 14; // vai al frame successivo
                frameCounter = 0;
            }
        } else {
            frameIndex = 0; // fermo → primo frame (idle)
        }
    }

    private BufferedImage normalizeSprite(BufferedImage original, int targetWidth, int targetHeight) {
        // Crea un'immagine vuota con sfondo trasparente
        BufferedImage normalized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = normalized.createGraphics();

        // Coordinate di partenza per allineare in basso
        int x = (targetWidth - original.getWidth()) / 2; // centro orizzontalmente
        int y = targetHeight - original.getHeight();     // piedi in basso

        g2.drawImage(original, x, y, null);
        g2.dispose();

        return normalized;
    }

    private BufferedImage scaleSprite(BufferedImage original, int scaleFactor) {
        int newW = original.getWidth() * scaleFactor;
        int newH = original.getHeight() * scaleFactor;

        BufferedImage scaled = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.drawImage(original, 0, 0, newW, newH, null);
        g2.dispose();
        return scaled;
    }
}
