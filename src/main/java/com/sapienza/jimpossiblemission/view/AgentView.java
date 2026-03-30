package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.Debug;
import com.sapienza.jimpossiblemission.model.Direction;
import com.sapienza.jimpossiblemission.model.AgentModel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.sapienza.jimpossiblemission.ImageUtils.flipHorizontal;

public class AgentView {
    private final AgentModel model;

    private final BufferedImage[] spritesRunRight = new BufferedImage[16];
    private final BufferedImage[] spritesRunLeft = new BufferedImage[16];
    private final BufferedImage[] spritesJumpRight = new BufferedImage[12];
    private final BufferedImage[] spritesJumpLeft = new BufferedImage[12];
    private final BufferedImage[] spritesElectrifiedRight = new BufferedImage[23];
    private final BufferedImage[] spritesElectrifiedLeft = new BufferedImage[23];
    private final BufferedImage searchSprite;

    int runFrameIndex = 0;
    int runFrameCounter = 0;
    int jumpFrameIndex = 0;
    int jumpFrameCounter = 0;
    int runFrameDelay = 4;
    int jumpFrameDelay = 4;
    Direction jumpDirection;

    public AgentView(AgentModel model, String agentDirectory) {
        this.model = model;
        try {
            for (int i = 0; i < 12; i++) {
                BufferedImage raw = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(agentDirectory + "/jump" + "/" + (i+1) + ".png")));
                spritesJumpRight[i] = raw;
                spritesJumpLeft[i] = flipHorizontal(raw);
            }
            for (int i = 0; i <= 15; i++) {
                BufferedImage raw = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(agentDirectory + "/run" + "/" + i + ".png")));
                spritesRunRight[i] = raw;
                spritesRunLeft[i] = flipHorizontal(raw);
            }
            for (int i = 0; i <= 22; i++) {
                BufferedImage raw = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(agentDirectory + "/electrified" + "/" + i + ".png")));
                spritesElectrifiedRight[i] = raw;
                spritesElectrifiedLeft[i] = flipHorizontal(raw);
            }
            searchSprite = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(agentDirectory + "/search" + "/search.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void draw(Graphics2D g) {
        BufferedImage image;
        Direction direction = model.getDirection();

        if (model.isJumping()) {
            // logica salto
            if (jumpFrameIndex == 0) {
                jumpDirection = direction;
            }
            jumpFrameCounter++;
            if (jumpFrameCounter >= jumpFrameDelay) {
                jumpFrameIndex = (jumpFrameIndex + 1) % 12;
                jumpFrameCounter = 0;
            }
            image = (jumpDirection == Direction.LEFT) ? spritesJumpLeft[jumpFrameIndex] : spritesJumpRight[jumpFrameIndex];

            runFrameIndex = 0;
            runFrameCounter = 0;

        } else if (model.isMoving()) {
            // logica corsa
            runFrameCounter++;
            if (runFrameCounter >= runFrameDelay) {
                runFrameIndex = (runFrameIndex + 1) % 16;
                runFrameCounter = 0;
            }
            image = (direction == Direction.LEFT) ? spritesRunLeft[runFrameIndex] : spritesRunRight[runFrameIndex];

            jumpFrameIndex = 0;
            jumpFrameCounter = 0;

        } else if (model.isInteracting()) {
            runFrameIndex = 0;
            runFrameCounter = 0;
            jumpFrameIndex = 0;
            jumpFrameCounter = 0;

            image = searchSprite;
        } else {
            // fermo (idle)
            runFrameIndex = 0;
            runFrameCounter = 0;
            jumpFrameIndex = 0;
            jumpFrameCounter = 0;
            image = (direction == Direction.LEFT) ? spritesRunLeft[runFrameIndex] : spritesRunRight[runFrameIndex];
        }

        g.drawImage(image, model.getX(), model.getY(), image.getWidth(), image.getHeight(), null);

        if (Debug.getInstance().isDebug()) {
            g.setColor(Color.RED);
            int marginX = 20; // margine laterale
            int marginY = 0;  // margine superiore e inferiore
            if (model.isJumping()) {
                g.drawRect(model.getX() + marginX, model.getY() + marginY + 50, image.getWidth() - 2 * marginX, image.getHeight() - 50);
            } else {
                g.drawRect(model.getX() + marginX, model.getY() + marginY, image.getWidth() - 2 * marginX, image.getHeight());
            }
        }
    }

}
