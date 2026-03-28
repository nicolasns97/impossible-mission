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

    static {
        containerSpriteMap = new HashMap<>();
        for (ContainerType type : ContainerType.values()) {
            try {
                BufferedImage sprite = ImageIO.read(Objects.requireNonNull(ContainerView.class.getResourceAsStream(String.format("/container/%s.png", type.name()))));
                containerSpriteMap.put(type, sprite);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public ContainerView(Container model) {
        this.model = model;
    }


    public void draw(Graphics2D g) {
        if (model.getSearchCompletion() < 100) {
            g.drawImage(containerSpriteMap.get(model.getType()), model.getX(), model.getY(), model.getWidth(), model.getHeight(), null);
        }
    }
}
