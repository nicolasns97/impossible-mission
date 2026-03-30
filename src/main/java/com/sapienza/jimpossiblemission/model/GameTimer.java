package com.sapienza.jimpossiblemission.model;

import javax.swing.*;

public class GameTimer {
    private int totalSeconds;
    private Timer swingTimer;
    private boolean timeUp = false;

    public GameTimer(int minutes, int seconds) {
        this.totalSeconds = (minutes * 60) + seconds;

        this.swingTimer = new Timer(1000, e -> {
            if (totalSeconds > 0) {
                totalSeconds--;
            } else {
                timeUp = true;
                ((Timer)e.getSource()).stop();
            }
        });
    }

    public void start() { swingTimer.start(); }
    public void stop() { swingTimer.stop(); }

    public String getFormattedTime() {
        int min = totalSeconds / 60;
        int sec = totalSeconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    public boolean isTimeUp() { return timeUp; }
}
