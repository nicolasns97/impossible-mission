package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.Debug;
import com.sapienza.jimpossiblemission.model.ElevatorModel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ElevatorView {
    private final ElevatorModel model;
    private final BufferedImage baseSprite;
    private final BufferedImage doorsSprite;
    private final BufferedImage roofSprite;

    public ElevatorView(ElevatorModel model, String baseSpritePath, String doorsSpritePath, String roofSpritePath) {
        this.model = model;
        try {
            baseSprite = ImageIO.read(Objects.requireNonNull(getClass().getResource(baseSpritePath)));
            doorsSprite = ImageIO.read(Objects.requireNonNull(getClass().getResource(doorsSpritePath)));
            roofSprite = ImageIO.read(Objects.requireNonNull(getClass().getResource(roofSpritePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void drawLift(Graphics2D g) {
        g.drawImage(baseSprite, model.getX(), model.getY(), baseSprite.getWidth(), baseSprite.getHeight(), null);
    }

    public void drawDoors(Graphics2D g) {
        g.drawImage(doorsSprite, model.getX(), model.getY(), doorsSprite.getWidth(), doorsSprite.getHeight(), null);
        if (Debug.getInstance().isDebug()) {
            g.setColor(Color.BLUE);
            g.drawRect(model.getX(), model.getY()+baseSprite.getHeight()-32, baseSprite.getWidth(), 32);
            g.drawRect(model.getX() + baseSprite.getWidth() / 2 - 16, model.getY()+baseSprite.getHeight()-32, 32, 32);
        }
    }

    public void drawRoof(Graphics2D g, int cameraY) {
        g.drawImage(roofSprite, 544, -cameraY, roofSprite.getWidth(), roofSprite.getHeight(), null);
    }

}
