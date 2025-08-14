package com.sapienza.jimpossiblemission;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Player {
    PlayerKeyHandler playerKeyHandler;
    GamePanel gamePanel;
    BufferedImage[] spritesRunRight = new BufferedImage[16];
    BufferedImage[] spritesRunLeft = new BufferedImage[16];
    int x;
    int y;
    int speed;
    String direction;

    int frameIndex = 0;        // quale frame stiamo disegnando
    int frameCounter = 0;      // per il tempo dell'animazione
    int frameDelay = 4;        // numero di cicli di game loop prima di passare al prossimo frame

    public Player(GamePanel gamePanel, PlayerKeyHandler playerKeyHandler) {
        this.gamePanel = gamePanel;
        this.playerKeyHandler = playerKeyHandler;
        this.x = 618;
        this.y = 217;
        this.speed = 5;
        this.direction = "left";
        loadSprite();
    }

    public void loadSprite() {
        try {
            for (int i = 0; i <= 15; i++) {
                BufferedImage raw = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/run/" + i + ".png")));
                spritesRunRight[i] = raw;
                spritesRunLeft[i] = flipHorizontal(raw);
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


    public static BufferedImage flipHorizontal(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();

        // Trasformazione: scala a -1 sull’asse X, poi trasla di w pixel
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-w, 0);

        // Applica la trasformazione
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
    }
}
