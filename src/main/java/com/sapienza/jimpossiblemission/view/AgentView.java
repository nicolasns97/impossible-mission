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
    BufferedImage[] spritesRunRight = new BufferedImage[16];
    BufferedImage[] spritesRunLeft = new BufferedImage[16];

    int frameIndex = 0;
    int frameCounter = 0;
    int frameDelay = 4;        // numero di cicli di game loop prima di passare al prossimo frame

    public AgentView(String agentDirectory) {
        try {
            for (int i = 0; i <= 15; i++) {
                BufferedImage raw = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(agentDirectory + RUN_DIR + "/" + i + ".png")));
                spritesRunRight[i] = raw;
                spritesRunLeft[i] = flipHorizontal(raw);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void draw(Graphics2D g, Direction direction, boolean moving, int x, int y) {
        if (moving) {
            frameCounter++;
            if (frameCounter >= frameDelay) {
                frameIndex = (frameIndex + 1) % 16; // vai al frame successivo
                frameCounter = 0;
            }
        } else {
            frameIndex = 0; // fermo → primo frame (idle)
        }
        BufferedImage image = null;
        switch (direction) {
            case LEFT -> image = spritesRunLeft[frameIndex];
            case RIGHT -> image = spritesRunRight[frameIndex];
        }
        g.drawImage(image, x, y, image.getWidth(), image.getHeight(), null);
        if (Debug.getInstance().isDebug()) {
            g.setColor(Color.RED);
            g.drawRect(x, y, image.getWidth(), image.getHeight());
            int marginX = 20; // margine laterale
            int marginY = 0;  // margine superiore e inferiore
            g.drawRect(x + marginX, y + marginY, image.getWidth() - 2 * marginX, image.getHeight()- 2 * marginY);
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
