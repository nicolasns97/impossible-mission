package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.model.Card;
import com.sapienza.jimpossiblemission.model.Code;
import com.sapienza.jimpossiblemission.model.Collectable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class CollectableView {
    private static final BufferedImage pointsSprite;
    private static final BufferedImage liftResetSprite;
    private static final BufferedImage snoozeSprite;
    private static final BufferedImage doorKeySprite;

    static {
        try {
            pointsSprite = ImageIO.read(Objects.requireNonNull(CollectableView.class.getResourceAsStream("/collectable/points.png")));
            liftResetSprite = ImageIO.read(Objects.requireNonNull(CollectableView.class.getResourceAsStream("/collectable/lift.png")));
            snoozeSprite = ImageIO.read(Objects.requireNonNull(CollectableView.class.getResourceAsStream("/collectable/snooze.png")));
            doorKeySprite = ImageIO.read(Objects.requireNonNull(CollectableView.class.getResourceAsStream("/collectable/key.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void draw(Graphics2D g, Collectable collectable, int agentX, int agentY) {
        BufferedImage image = null;
        if (collectable instanceof Card) {
            image = pointsSprite;
        } else if (collectable instanceof Code) {
            Code code = (Code) collectable;
            switch (code.getType()) {
                case SNOOZE -> image = snoozeSprite;
                case PLATFORM_RESET -> image = liftResetSprite;
                case DOOR_KEY -> image = doorKeySprite;
            }
        }
        g.drawImage(image, agentX + 100, agentY + 50, 150, 80, null);
    }
}
