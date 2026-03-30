package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.model.Container;
import com.sapienza.jimpossiblemission.model.Container.ContainerType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ContainerView {
    private final Container model;
    private static final Map<ContainerType, BufferedImage> containerSpriteMap;
    private static final BufferedImage searchingSprite;

    static {
        containerSpriteMap = new HashMap<>();
        try {
            searchingSprite = ImageIO.read(Objects.requireNonNull(ContainerView.class.getResourceAsStream("/container/searching.png")));
            for (ContainerType type : ContainerType.values()) {
                BufferedImage sprite = ImageIO.read(Objects.requireNonNull(ContainerView.class.getResourceAsStream(String.format("/container/%s.png", type.name()))));
                containerSpriteMap.put(type, sprite);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public ContainerView(Container model) {
        this.model = model;
    }


    public void draw(Graphics2D g, int agentX, int agentY) {
        if (model.getSearchCompletion() < 100) {
            g.drawImage(containerSpriteMap.get(model.getType()), model.getX(), model.getY(), model.getWidth(), model.getHeight(), null);

            if (model.isInteracting()) {
                drawSearchingOverlay(g, agentX, agentY);
            }
        }
    }

    private void drawSearchingOverlay(Graphics2D g, int agentX, int agentY) {
        int overlayX = agentX + 100;
        int overlayY = agentY + 50;
        int overlayWidth = 150;
        int overlayHeight = 80;
        g.drawImage(searchingSprite, overlayX, overlayY, overlayWidth, overlayHeight, null);
        double remainingPercent = (100.0 - model.getSearchCompletion()) / 100.0;
        int currentWidth = (int) (remainingPercent * overlayWidth);
        if (currentWidth < 0) currentWidth = 0;
        g.setColor(Color.BLACK);
        g.fillRect(overlayX, overlayY+50, currentWidth, 15);
    }
}
