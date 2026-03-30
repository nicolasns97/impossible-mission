package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.model.GameSession;
import com.sapienza.jimpossiblemission.model.Life;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class HUDView {
    private final GameSession session;

    private static BufferedImage heartFull;
    private static BufferedImage heartEmpty;

    static {
        try {
            heartFull = ImageIO.read(Objects.requireNonNull(HUDView.class.getResourceAsStream("/hud/heart-full.png")));
            heartEmpty = ImageIO.read(Objects.requireNonNull(HUDView.class.getResourceAsStream("/hud/heart-empty.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HUDView(GameSession session) {
        this.session = session;
    }

    public void draw(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int screenWidth = 1280;
        int margin = 50;
        int yTextPos = 35;


        // 3. DISEGNO TIMER (In alto a SINISTRA)
        g.setFont(new Font("Monospaced", Font.BOLD, 22));
        g.setColor(Color.BLACK);

        String timeStr = "TIME: " + session.getTimer().getFormattedTime();
        g.drawString(timeStr, margin, yTextPos);

        int heartSize = 25;
        int heartSpacing = 30;
        int totalLives = session.getLives().size();
        int startHeartsX = screenWidth - margin - (totalLives * heartSpacing) + (heartSpacing - heartSize);

        int drawCount = 0; // Contatore per gestire la posizione X progressiva

        // --- PRIMO PASSAGGIO: Disegna i cuori VUOTI (isUsed == true) ---
        for (Life life : session.getLives()) {
            if (life.isUsed()) {
                int currentHeartX = startHeartsX + (drawCount * heartSpacing);
                drawHeart(g, heartEmpty, currentHeartX, yTextPos - 22, heartSize, Color.DARK_GRAY);
                drawCount++;
            }
        }

        // --- SECONDO PASSAGGIO: Disegna i cuori PIENI (isUsed == false) ---
        for (Life life : session.getLives()) {
            if (!life.isUsed()) {
                int currentHeartX = startHeartsX + (drawCount * heartSpacing);
                drawHeart(g, heartFull, currentHeartX, yTextPos - 22, heartSize, Color.RED);
                drawCount++;
            }
        }
    }

    private void drawHeart(Graphics2D g, BufferedImage img, int x, int y, int size, Color fallbackColor) {
        if (img != null) {
            g.drawImage(img, x, y, size, size, null);
        } else {
            g.setColor(fallbackColor);
            g.fillOval(x, y, size, size);
        }
    }
}
