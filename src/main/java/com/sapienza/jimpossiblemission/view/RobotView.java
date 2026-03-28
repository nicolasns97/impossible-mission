package com.sapienza.jimpossiblemission.view;

import com.sapienza.jimpossiblemission.model.PatrollingRobot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class RobotView {
    private final PatrollingRobot model;
    private BufferedImage[] idle_left = new BufferedImage[2];
    private BufferedImage[] idle_right = new BufferedImage[2];
    private BufferedImage[] rotate = new BufferedImage[5];

    private enum State { IDLE, ROTATE }
    private State state = State.IDLE;

    private int frameIndex = 0;
    private int frameCounter = 0;
    private final int idleFrameDelay = 30;   // blink lento
    private final int rotateFrameDelay = 8;  // rotazione veloce

    private int idleCycles = 0; // conta quante volte è rimasto in idle prima di ruotare
    private final int idleBeforeRotate = 5; // dopo 5 "cicli idle" inizia rotazione

    public RobotView(PatrollingRobot model) {
        this.model = model;
        try {
            // TODO performance
            idle_left[0] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/robot/robot_idle_lx.png")));
            idle_left[1] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/robot/robot_idle_blink_lx.png")));
            idle_right[0] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/robot/robot_idle_rx.png")));
            idle_right[1] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/robot/robot_idle_blink_rx.png")));
            rotate[0] =  ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/robot/robot_rotate_lx_1.png")));
            rotate[1] =  ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/robot/robot_rotate_lx_2.png")));
            rotate[2] =  ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/robot/robot_rotate_3.png")));
            rotate[3] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/robot/robot_rotate_rx_2.png")));
            rotate[4] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/robot/robot_rotate_rx_1.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void draw(Graphics2D g) {
        frameCounter++;

        int currentDelay = (state == State.ROTATE) ? rotateFrameDelay : idleFrameDelay;

        if (frameCounter >= currentDelay) {
            frameCounter = 0;

            switch (state) {
                case IDLE -> {
                    frameIndex = (frameIndex + 1) % idle_left.length;
                    idleCycles++;
                    if (idleCycles >= idleBeforeRotate) {
                        idleCycles = 0;
                        state = State.ROTATE;
                        frameIndex = 0;
                    }
                }
                case ROTATE -> {
                    frameIndex++;
                    if (frameIndex >= rotate.length) {
                        frameIndex = 0;
                        state = State.IDLE;
                    }
                }
            }
        }

        if (state == State.IDLE) {
            g.drawImage(idle_left[frameIndex], model.getX(), model.getY(), 75, 75, null);
        } else if (state == State.ROTATE) {
            g.drawImage(rotate[frameIndex], model.getX(), model.getY(), 75, 75, null);
        }
    }
}
