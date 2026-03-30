package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.model.TerminalMenu;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class TerminalMenuView {
    private final TerminalMenu model;
    private static final BufferedImage menuSprite;
    private static final BufferedImage arrowSprite;

    static {
        try {
            menuSprite = ImageIO.read(Objects.requireNonNull(TerminalMenuView.class.getResourceAsStream("/terminal/menu.png")));
            arrowSprite = ImageIO.read(Objects.requireNonNull(TerminalMenuView.class.getResourceAsStream("/terminal/arrow.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public TerminalMenuView(TerminalMenu model) {
        this.model = model;
    }

    public void draw(Graphics2D g2d) {
        g2d.drawImage(menuSprite, 0, 0, menuSprite.getWidth(), menuSprite.getHeight(), null);
        int arrowY = 0;
        if (model.getMenuIndex() == 2) {
            arrowY = 432;
        } else if (model.getMenuIndex() == 1) {
            arrowY = 250;
        } else {
            arrowY = 175;
        }
        g2d.drawImage(arrowSprite, 190, arrowY, arrowSprite.getWidth(), arrowSprite.getHeight(), null);
        if (model.isShowPasswordRequired()) {
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 50));
            g2d.drawString("PASSWORD REQUIRED", 375, 375);
        }
    }
}
