package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.Debug;
import com.sapienza.jimpossiblemission.model.Direction;
import com.sapienza.jimpossiblemission.model.Enemy;
import com.sapienza.jimpossiblemission.model.LaserRobot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.sapienza.jimpossiblemission.ImageUtils.flipHorizontal;

public class EnemyView {
    private final Enemy model;
    private static final BufferedImage[] idle_left = new BufferedImage[2];
    private static final BufferedImage[] idle_right = new BufferedImage[2];
    private static final BufferedImage[] rotate = new BufferedImage[5];
    private static final BufferedImage[] laser_left = new BufferedImage[5];
    private static final BufferedImage[] laser_right = new BufferedImage[5];

    private enum State { IDLE, ROTATE, SHOOTING }
    private State state = State.IDLE;

    private int frameIndex = 0;
    private int frameCounter = 0;
    private final int idleFrameDelay = 30;
    private final int rotateFrameDelay = 6; // Rotazione fluida

    // Tracciamento direzione per attivare la rotazione
    private Direction lastDirection;
    private boolean isReversingRotation = false;

    static {
        try {
            idle_left[0] = ImageIO.read(Objects.requireNonNull(EnemyView.class.getResourceAsStream("/robot/robot_idle_lx.png")));
            idle_left[1] = ImageIO.read(Objects.requireNonNull(EnemyView.class.getResourceAsStream("/robot/robot_idle_blink_lx.png")));
            idle_right[0] = ImageIO.read(Objects.requireNonNull(EnemyView.class.getResourceAsStream("/robot/robot_idle_rx.png")));
            idle_right[1] = ImageIO.read(Objects.requireNonNull(EnemyView.class.getResourceAsStream("/robot/robot_idle_blink_rx.png")));

            // Caricamento rotazione (da sinistra a destra)
            rotate[0] = ImageIO.read(Objects.requireNonNull(EnemyView.class.getResourceAsStream("/robot/robot_rotate_lx_1.png")));
            rotate[1] = ImageIO.read(Objects.requireNonNull(EnemyView.class.getResourceAsStream("/robot/robot_rotate_lx_2.png")));
            rotate[2] = ImageIO.read(Objects.requireNonNull(EnemyView.class.getResourceAsStream("/robot/robot_rotate_3.png")));
            rotate[3] = ImageIO.read(Objects.requireNonNull(EnemyView.class.getResourceAsStream("/robot/robot_rotate_rx_2.png")));
            rotate[4] = ImageIO.read(Objects.requireNonNull(EnemyView.class.getResourceAsStream("/robot/robot_rotate_rx_1.png")));

            for (int i = 0; i <= 4; i++) {
                BufferedImage raw = ImageIO.read(Objects.requireNonNull(EnemyView.class.getResourceAsStream("/robot/laser/laser_lx_" + i + ".png")));
                laser_left[i] = raw;
                laser_right[i] = flipHorizontal(raw);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public EnemyView(Enemy model) {
        this.model = model;
        this.lastDirection = model.getDirection();
    }

    public void draw(Graphics2D g) {
        // 1. Logica di cambio Stato
        if (model instanceof LaserRobot) {
            state = ((LaserRobot) model).isFiring() ? State.SHOOTING : State.IDLE;
        } else if (state != State.ROTATE) {
            // Se la direzione del modello cambia, attiviamo la rotazione
            if (model.getDirection() != lastDirection) {
                state = State.ROTATE;
                frameCounter = 0;

                // Se il modello ora guarda a SINISTRA, significa che venivamo da destra
                // quindi dobbiamo fare l'animazione al contrario (da 4 a 0)
                isReversingRotation = (model.getDirection() == Direction.LEFT);
                frameIndex = isReversingRotation ? rotate.length - 1 : 0;

                lastDirection = model.getDirection();
            } else {
                state = State.IDLE;
            }
        }

        // 2. Gestione Timer Animation
        frameCounter++;
        int currentDelay = switch (state) {
            case SHOOTING -> 6;
            case ROTATE -> rotateFrameDelay;
            default -> idleFrameDelay;
        };

        if (frameCounter >= currentDelay) {
            frameCounter = 0;
            updateAnimationState();
        }

        // 3. Selezione e Disegno Immagine
        boolean isLeft = (model.getDirection() == Direction.LEFT);
        BufferedImage robotBase;

        if (state == State.ROTATE) {
            robotBase = rotate[frameIndex];
        } else {
            // Usa il modulo per evitare errori se frameIndex del laser > 1
            int idleIdx = frameIndex % idle_left.length;
            robotBase = isLeft ? idle_left[idleIdx] : idle_right[idleIdx];
        }

        // Disegna prima il robot
        g.drawImage(robotBase, model.getX(), model.getY(), 75, 75, null);

        // 4. Sovrapposizione del LASER (Solo se sta sparando)
        if (state == State.SHOOTING) {
            BufferedImage laserSprite = isLeft ? laser_left[frameIndex] : laser_right[frameIndex];
            if (isLeft) {
                g.drawImage(laserSprite, model.getX()-140, model.getY(), 150, 75, null);
            } else {
                g.drawImage(laserSprite, model.getX()+65, model.getY(), 150, 75, null);
            }
        }

        if (Debug.getInstance().isDebug()) {
            Rectangle boundingBox = model.getBoundingBox();
            g.drawRect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        }
    }

    private void updateAnimationState() {
        switch (state) {
            case ROTATE -> {
                if (isReversingRotation) {
                    frameIndex--;
                    if (frameIndex < 0) finishRotation();
                } else {
                    frameIndex++;
                    if (frameIndex >= rotate.length) finishRotation();
                }
            }
            case SHOOTING -> frameIndex = (frameIndex + 1) % laser_left.length;
            case IDLE -> frameIndex = (frameIndex + 1) % idle_left.length;
        }
    }

    private void finishRotation() {
        state = State.IDLE;
        frameIndex = 0;
    }
}