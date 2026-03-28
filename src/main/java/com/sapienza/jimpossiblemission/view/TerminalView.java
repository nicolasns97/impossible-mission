package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.model.Terminal;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class TerminalView {
    private final Terminal model;
    private static final BufferedImage terminalSprite;

    static {
        try {
            terminalSprite = ImageIO.read(Objects.requireNonNull(TerminalView.class.getResourceAsStream("/terminal/terminal.png")));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public TerminalView(Terminal model) {
        this.model = model;
    }


    public void draw(Graphics2D g) {
        g.drawImage(terminalSprite, model.getX(), model.getY(), model.getWidth(), model.getHeight(), null);
    }
}
