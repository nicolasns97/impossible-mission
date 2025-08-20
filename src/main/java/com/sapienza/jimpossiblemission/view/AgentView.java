package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.Debug;
import com.sapienza.jimpossiblemission.Direction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class AgentView {
    private final String RUN_DIR = "/run";
    private final String JUMP_DIR = "/jump";

    BufferedImage[] spritesRunRight = new BufferedImage[16];
    BufferedImage[] spritesRunLeft = new BufferedImage[16];
    BufferedImage[] spritesJumpRight = new BufferedImage[12];
    BufferedImage[] spritesJumpLeft = new BufferedImage[12];

    int runFrameIndex = 0;
    int runFrameCounter = 0;
    int jumpFrameIndex = 0;
    int jumpFrameCounter = 0;
    int runFrameDelay = 4;
    int jumpFrameDelay = 4;

    public AgentView(String agentDirectory) {
        try {
            for (int i = 0; i < 12; i++) {
                BufferedImage raw = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(agentDirectory + JUMP_DIR + "/" + (i+1) + ".png")));
                spritesJumpRight[i] = raw;
                spritesJumpLeft[i] = flipHorizontal(raw);
            }
            for (int i = 0; i <= 15; i++) {
                BufferedImage raw = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(agentDirectory + RUN_DIR + "/" + i + ".png")));
                spritesRunRight[i] = raw;
                spritesRunLeft[i] = flipHorizontal(raw);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void draw(Graphics2D g, Direction direction, boolean moving, int x, int y, boolean jumping) {
        BufferedImage image = null;

        if (jumping) {
            // logica salto
            jumpFrameCounter++;
            if (jumpFrameCounter >= jumpFrameDelay) {
                jumpFrameIndex = (jumpFrameIndex + 1) % 12;
                jumpFrameCounter = 0;
            }
            image = (direction == Direction.LEFT) ? spritesJumpLeft[jumpFrameIndex] : spritesJumpRight[jumpFrameIndex];

            // opzionale: reset frame corsa per evitare salto subito dopo frame sbagliato
            runFrameIndex = 0;
            runFrameCounter = 0;

        } else if (moving) {
            // logica corsa
            runFrameCounter++;
            if (runFrameCounter >= runFrameDelay) {
                runFrameIndex = (runFrameIndex + 1) % 16;
                runFrameCounter = 0;
            }
            image = (direction == Direction.LEFT) ? spritesRunLeft[runFrameIndex] : spritesRunRight[runFrameIndex];

            // opzionale: reset frame salto per evitare glitch se subito dopo si salta
            jumpFrameIndex = 0;
            jumpFrameCounter = 0;

        } else {
            // fermo (idle)
            runFrameIndex = 0;
            runFrameCounter = 0;
            jumpFrameIndex = 0;
            jumpFrameCounter = 0;
            image = (direction == Direction.LEFT) ? spritesRunLeft[runFrameIndex] : spritesRunRight[runFrameIndex];
        }

        g.drawImage(image, x, y, image.getWidth(), image.getHeight(), null);

        if (Debug.getInstance().isDebug()) {
            g.setColor(Color.RED);
            int marginX = 20; // margine laterale
            int marginY = 0;  // margine superiore e inferiore
            if (jumping) {
                g.drawRect(x + marginX, y + marginY + 50, image.getWidth() - 2 * marginX, image.getHeight() - 50);
            } else {
                g.drawRect(x + marginX, y + marginY, image.getWidth() - 2 * marginX, image.getHeight());
            }
        }
    }

    public static BufferedImage flipHorizontal(BufferedImage img) {
        int w = img.getWidth();

        // Trasformazione: scala a -1 sull’asse X, poi trasla di w pixel
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-w, 0);

        // Applica la trasformazione
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
    }
}
