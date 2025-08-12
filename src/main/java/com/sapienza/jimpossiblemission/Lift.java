package com.sapienza.jimpossiblemission;

import org.mapeditor.core.MapObject;
import org.mapeditor.core.ObjectGroup;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Lift {
    int x,y, speed;
    BufferedImage sprite;
    BufferedImage sprite_2;
    BufferedImage sprite_3;
    LiftKeyHandler liftKeyHandler;
    GamePanel gamePanel;
    private int liftUpperBound;
    private int liftLowerBound;
    public boolean moving;

    public Lift(GamePanel gamePanel, LiftKeyHandler liftKeyHandler) {
        this.liftKeyHandler = liftKeyHandler;
        this.gamePanel = gamePanel;
        this.x = 544;
        this.y = 96;
        liftUpperBound = y;
        liftLowerBound = y;
        speed = 9;
        loadSprite();
        loadLiftBounds();
    }

    private void loadLiftBounds() {
        ObjectGroup objectGroup = (ObjectGroup ) gamePanel.map.getLayer(1);
        for (MapObject mapObject : objectGroup.getObjects()) {
            if (mapObject.getY() < liftUpperBound) {
                liftUpperBound = (int) mapObject.getY();
            }
            if (mapObject.getY() > liftLowerBound) {
                liftLowerBound = (int) mapObject.getY();
            }
        }
    }

    public void drawLift(Graphics2D g) {
        g.drawImage(sprite_3,x,y,sprite.getWidth(),sprite.getHeight(),null);
    }

    public void drawDoors(Graphics2D g) {
        g.drawImage(sprite_2,x,y,sprite.getWidth(),sprite.getHeight(),null);
    }

    public void update() {
        ObjectGroup objectGroup = (ObjectGroup ) gamePanel.map.getLayer(1);
        int moveY = 0; // somma totale di movimento per questo frame
        moving = false;
        if (liftKeyHandler.up && (y != liftUpperBound - gamePanel.cameraY)) {
            moving = true;
            moveY = -speed;
        } else if (liftKeyHandler.down && (y != liftLowerBound - gamePanel.cameraY)) {
            moving = true;
            moveY = speed;
        }

        // Movimento passo passo per evitare di saltare la condizione di "completamente dentro"
        int step = (moveY > 0) ? 1 : -1; // direzione del movimento
        int steps = Math.abs(moveY);

        for (int i = 0; i < steps; i++) {
            // muovo la camera di 1px
            gamePanel.cameraY += step;

            // controllo collisione con contains
            for (MapObject mapObject : objectGroup.getObjects()) {
                Rectangle objectRect = new Rectangle(
                        (int) mapObject.getX() - gamePanel.cameraX,
                        (int) mapObject.getY() - gamePanel.cameraY,
                        mapObject.getWidth().intValue(),
                        mapObject.getHeight().intValue()
                );

                Rectangle liftRect = new Rectangle(
                        x,
                        y,
                        sprite.getWidth(),
                        sprite.getHeight()
                );

                if (objectRect.contains(liftRect)) {
                    System.out.println("Collision - completamente dentro");
                    liftKeyHandler.up = false;
                    liftKeyHandler.down = false;
                    return; // blocca qui il movimento
                }
            }
        }
        /**
        if (keyHandler.up) {
            gamePanel.cameraY -= speed;
        }
        if (keyHandler.down) {
            gamePanel.cameraY += speed;
        }
        */
    }

    public void loadSprite() {
        try {
            sprite = ImageIO.read(Objects.requireNonNull(getClass().getResource("/lift.png")));
            sprite_2= ImageIO.read(Objects.requireNonNull(getClass().getResource("/lift2.png")));
            sprite_3 = ImageIO.read(Objects.requireNonNull(getClass().getResource("/lift3.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getCenterY() {
        return y + sprite.getHeight() / 2;
    }


}
