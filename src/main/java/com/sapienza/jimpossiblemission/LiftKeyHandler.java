package com.sapienza.jimpossiblemission;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LiftKeyHandler implements KeyListener {
    public boolean up, down;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_UP) {
            up = true;
            down = false;
        }
        if (code == KeyEvent.VK_DOWN) {
            down = true;
            up = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
